#!/usr/bin/env bash
# Ubuntu 22.04, root, offline. Collect evidence and a complete local MariaDB dump.
# No service control, mount operations, database-row changes or image modifications.
# Usage: bash collect_image_diagnostics.sh [existing-output-parent-directory]
# Optional: CPCLOUD_URL=http://cpcloud-host:11111 OPENSTACK_RC=/root/admin-openrc.sh
set -uo pipefail
export LC_ALL=C

PART_BYTES=9000000
ZIP_LIMIT=10000000
LOG_BYTES=2097152

say() { printf '[%s] %s\n' "$(date '+%F %T')" "$*"; }
warn() { printf '%s\n' "$*" >> "$REPORT/warnings.txt"; say "WARN: $*"; }

redact() {
    sed -E \
        -e 's#(header:[[:space:]]*)\[[^]]*\]#\1[REDACTED]#Ig' \
        -e 's#(://[^/@:[:space:]]+:)[^@[:space:]]+@#\1[REDACTED]@#g' \
        -e 's#("(password|passwd|secret|token|ticket|access_token|secret_key|api_key)"[[:space:]]*:[[:space:]]*")[^"]*"#\1[REDACTED]"#Ig' \
        -e 's#^([[:space:]]*(password|passwd|os_password|secret|secret_key|api_key|access_token|token)[[:space:]]*[:=][[:space:]]*).*#\1[REDACTED]#Ig' \
        -e 's#((password|passwd|os_password|secret_key|api_key|access_token|token)[[:space:]]*=[[:space:]]*)[^,;[:space:]]+#\1[REDACTED]#Ig' \
        -e 's#(Authorization:|Cookie:|Set-Cookie:).*#\1 [REDACTED]#Ig' \
        -e 's#([?&](token|ticket|password)=)[^&[:space:]]+#\1[REDACTED]#Ig'
}

# Optional commands are recorded as unavailable rather than installed.
capture() {
    local file=$1 rc
    shift
    if ! command -v "$1" >/dev/null 2>&1; then
        printf 'UNAVAILABLE: %s\n' "$1" > "$REPORT/$file"
        return 0
    fi
    timeout 40s "$@" > "$REPORT/$file.raw" 2>&1
    rc=$?
    redact < "$REPORT/$file.raw" > "$REPORT/$file"
    rm -f -- "$REPORT/$file.raw"
    printf '\n[command exit status: %s; 124 means timeout]\n' "$rc" >> "$REPORT/$file"
}

collect_config() {
    local file=$1 key path
    [[ -f "$file" && -r "$file" ]] || return 0
    [[ -z ${SEEN_CONFIGS[$file]+x} ]] || return 0
    SEEN_CONFIGS[$file]=1
    {
        printf '\n===== %s =====\n' "$file"
        stat -c 'owner=%U:%G mode=%a size=%s modified=%y' -- "$file"
        head -c 262144 -- "$file" | redact
    } >> "$REPORT/configs/selected_configs.txt"
    # Glance may have several file stores; an optional :priority follows the path.
    while IFS= read -r path; do
        [[ "$path" == /* ]] && IMAGE_DIRS["$path"]=1
    done < <(awk -F= '
        /^[[:space:]]*filesystem_store_datadirs?[[:space:]]*=/ {
            p=substr($0,index($0,"=")+1); sub(/[[:space:]]*[#;].*$/,"",p)
            gsub(/^[[:space:]\047\042]+|[[:space:]\047\042]+$/,"",p)
            sub(/:[0-9]+$/,"",p); if(p!="") print p
        }' "$file")
    while IFS= read -r path; do
        [[ "$path" == /* && -f "$path" ]] && LOG_FILES["$path"]=1
    done < <(awk -F= '
        /^[[:space:]]*(stdout_logfile|stderr_logfile|logfile)[[:space:]]*=/ {
            p=substr($0,index($0,"=")+1); gsub(/^[[:space:]]+|[[:space:]]+$/,"",p)
            if(p!="AUTO" && p!="NONE") print p
        }' "$file")
}

collect_files_and_processes() {
    local root file pid args dir fd n=0
    local -a roots=()
    for root in /etc/glance /etc/nova /etc/cinder /etc/ceph /etc/supervisor /etc/supervisord.d \
                /etc/kolla /opt /data /home /root /var/log; do
        [[ -d "$root" ]] && roots+=("$root")
    done
    # Bounded discovery: do not scan VM disks, database data or image contents.
    timeout 60s find "${roots[@]}" -maxdepth 5 \
        \( -path "$OUT" -o -name .git -o -name node_modules -o -name .cache \
           -o -name .m2 -o -name .gradle -o -name images -o -name instances \
           -o -name site-packages -o -name dist-packages -o -name mysql \) -prune -o \
        -type f \( -name 'glance*.conf' -o -name 'nova.conf' -o -name 'cinder.conf' \
           -o -name 'ceph.conf' -o -name '*supervisor*.conf' -o -path '/etc/supervisor/conf.d/*' \
           -o -path '/etc/supervisord.d/*' -o -name 'application*.yml' -o -name 'application*.yaml' \
           -o -iname '*cpcloud*.conf' -o -iname '*cpcloud*.ini' \
           -o -name 'mp.log' -o -name 'bc.log' -o -iname '*cpcloud*.log' \
           -o -name 'glance-api.log' -o -name 'nova-compute.log' -o -name 'nova-api.log' \
           -o -name 'supervisord.log' -o -name 'error.log' \) -print0 \
        > "$OUT/discovered-files.nul" 2> "$REPORT/configs/discovery_errors.txt"
    while IFS= read -r -d '' file; do
        case "$file" in
            *.log) LOG_FILES["$file"]=1 ;;
            *) collect_config "$file" ;;
        esac
    done < "$OUT/discovered-files.nul"
    rm -f -- "$OUT/discovered-files.nul"
    for file in /etc/fstab /etc/crypttab /etc/supervisord.conf /etc/glance/glance-api.conf \
                /etc/kolla/glance-api/glance-api.conf; do
        collect_config "$file"
    done

    while read -r pid args; do
        [[ "$pid" =~ ^[0-9]+$ ]] || continue
        [[ "$args" =~ cpcloud|glance|nova-api|nova-compute|supervisord|gunicorn|uwsgi|mp\.jar ]] || continue
        ((n+=1)); ((n <= 40)) || break
        {
            printf '\nPID=%s COMMAND=' "$pid"; printf '%s\n' "$args" | redact
            for fd in exe cwd root; do printf '%s=' "$fd"; readlink "/proc/$pid/$fd" || true; done
            if [[ -r /proc/$pid/environ ]]; then
                tr '\0' '\n' < "/proc/$pid/environ" | \
                    grep -E '^(VIRTUAL_ENV|DJANGO_SETTINGS_MODULE|OS_AUTH_URL|OS_PROJECT_NAME|OS_TENANT_NAME|OS_USERNAME|OS_CLOUD|OS_INTERFACE)=' || true
            fi
        } >> "$REPORT/processes/relevant_processes.txt" 2>&1
        dir=$(readlink "/proc/$pid/cwd" 2>/dev/null) || continue
        if [[ -d "$dir" && "$dir" != / ]]; then
            while IFS= read -r -d '' file; do collect_config "$file"; done < <(
                timeout 10s find "$dir" -maxdepth 3 -type f \
                    \( -name 'settings.py' -o -name 'local_settings.py' -o -name 'glance*.conf' \
                    -o -name 'application*.yml' -o -name '*cpcloud*.ini' \) -print0 2>/dev/null)
        fi
        for fd in 1 2; do
            file=$(readlink "/proc/$pid/fd/$fd" 2>/dev/null) || continue
            [[ "$file" == /* && -f "$file" ]] && LOG_FILES["$file"]=1
        done
    done < <(ps -eo pid=,args= 2>/dev/null)

    n=0
    while IFS=$'\t' read -r _ file; do
        ((n+=1)); ((n <= 40)) || { warn 'Log sampling limited to 40 files.'; break; }
        printf '%s\t%s\n' "$n" "$file" >> "$REPORT/logs/index.tsv"
        { stat -c 'file=%n size=%s modified=%y' -- "$file"; tail -c "$LOG_BYTES" -- "$file"; } \
            2>&1 | redact > "$REPORT/logs/log_$(printf '%03d' "$n").txt"
        timeout 20s grep -nEi 'ImageNotFound|image.*(not found|does not exist|missing)|No such file|No space left|Permission denied|Input/output error|Read-only file system|未找到.*镜像|镜像.*不存在|找不到.*镜像' "$file" \
            2>/dev/null | tail -n 200 | awk '{print substr($0,1,4000)}' | redact \
            > "$REPORT/logs/log_$(printf '%03d' "$n").image_errors.txt"
    done < <(
        for file in "${!LOG_FILES[@]}"; do
            case "$file" in
                *cpcloud*|*glance*|*nova*|*/mp.log|*/bc.log) printf '0\t%s\n' "$file" ;;
                *) printf '1\t%s\n' "$file" ;;
            esac
        done | sort -k1,1n -k2,2
    )
}

inspect_image_dirs() {
    local dir n=0
    for dir in "${!IMAGE_DIRS[@]}"; do
        ((n+=1))
        {
            printf 'PATH=%s\n' "$dir"
            if [[ ! -d "$dir" ]]; then
                printf 'NOT_PRESENT_ON_THIS_HOST\n'
                continue
            fi
            stat -Lc 'owner=%U:%G mode=%a inode=%i device=%D modified=%y' -- "$dir"
            df -hT -- "$dir"; df -i -- "$dir"
            if command -v findmnt >/dev/null 2>&1; then findmnt -T "$dir" -o TARGET,SOURCE,FSTYPE,OPTIONS,UUID; fi
            if command -v namei >/dev/null 2>&1; then namei -l -- "$dir"; fi
            printf '\nFILE METADATA ONLY (maximum 10000 entries; no image data read):\n'
            timeout 30s find "$dir" -xdev -maxdepth 2 -type f \
                -printf '%p\t%s\t%u\t%g\t%TY-%Tm-%Td %TH:%TM:%TS\n' | head -n 10000
        } > "$REPORT/storage/image_dir_$(printf '%03d' "$n").txt" 2>&1
    done
}

http_get() {
    local url=$1 host port path
    if command -v curl >/dev/null 2>&1; then
        curl --noproxy '*' --connect-timeout 5 --max-time 25 -sS -i "$url"
    elif [[ "$url" =~ ^http://([^/:]+)(:([0-9]+))?(/.*)$ ]]; then
        host=${BASH_REMATCH[1]}; port=${BASH_REMATCH[3]:-80}; path=${BASH_REMATCH[4]}
        # Bash /dev/tcp supplies an HTTP GET when curl is not installed.
        timeout 25s bash -c '
            exec 3<>/dev/tcp/"$1"/"$2" || exit 1
            printf "GET %s HTTP/1.0\r\nHost: %s\r\nConnection: close\r\n\r\n" "$3" "$1" >&3
            cat <&3
        ' _ "$host" "$port" "$path"
    else
        printf 'UNAVAILABLE: HTTPS probe needs an existing curl installation. URL=%s\n' "$url"
    fi
}

collect_openstack() {
    local rc_file=${OPENSTACK_RC:-} candidate id n=0 started=$SECONDS
    if ! command -v openstack >/dev/null 2>&1; then
        printf 'UNAVAILABLE: openstack; database/configuration/storage collection still performed.\n' > "$REPORT/openstack/status.txt"
        return
    fi
    if [[ -z ${OS_AUTH_URL:-} && -z ${OS_CLOUD:-} && -z "$rc_file" ]]; then
        for candidate in /root/admin-openrc.sh /root/admin-openrc /root/keystonerc_admin \
                         /root/openrc /etc/kolla/admin-openrc.sh; do
            if [[ -f "$candidate" ]]; then rc_file=$candidate; break; fi
        done
    fi
    printf 'Authentication: inherit exported OS_* / clouds.yaml; RC file=%s\n' "${rc_file:-none}" > "$REPORT/openstack/status.txt"
    # Load the administrator-provided RC only in child shells, without terminal input.
    os_command() {
        timeout 60s bash -c '
            rc_file=$1; shift
            if [[ -n "$rc_file" ]]; then source "$rc_file" </dev/null || exit; fi
            exec openstack "$@"
        ' _ "$rc_file" "$@"
    }
    os_command image list --long -f json > "$REPORT/openstack/images.json" 2> "$REPORT/openstack/images.stderr"
    if [[ $? -ne 0 ]]; then
        warn 'OpenStack image list failed; inspect openstack/images.stderr and the authentication configuration.'
    else
        os_command image list -f value -c ID > "$REPORT/openstack/image_ids.txt" 2>> "$REPORT/openstack/images.stderr"
        while IFS= read -r id; do
            [[ "$id" =~ ^[a-zA-Z0-9_-]+$ ]] || continue
            ((SECONDS - started < 300)) || { warn 'OpenStack image detail collection reached its 300-second budget; the full image list is retained.'; break; }
            ((n+=1)); ((n <= 2000)) || { warn 'OpenStack image show limited to 2000 images; the full image list is retained.'; break; }
            os_command image show "$id" -f json > "$REPORT/openstack/image_$id.json" 2> "$REPORT/openstack/image_$id.stderr"
        done < "$REPORT/openstack/image_ids.txt"
    fi
    local -a command
    for candidate in 'compute service list' 'volume service list' 'appcontainer service list' \
                     'appcontainer list' 'server list --all-projects --long'; do
        read -r -a command <<< "$candidate"
        os_command "${command[@]}" -f json > "$REPORT/openstack/${candidate// /_}.json" \
            2> "$REPORT/openstack/${candidate// /_}.stderr"
    done
    # Nova's fault field is only in server show, not the ordinary server list.
    os_command server list --all-projects --status ERROR -f value -c ID \
        > "$REPORT/openstack/error_server_ids.txt" 2> "$REPORT/openstack/error_server_ids.stderr"
    n=0; started=$SECONDS
    while IFS= read -r id; do
        [[ "$id" =~ ^[a-zA-Z0-9_-]+$ ]] || continue
        ((n+=1))
        ((n <= 50 && SECONDS - started < 120)) || { warn 'Nova ERROR server detail collection reached its sampling limit.'; break; }
        os_command server show "$id" -f json > "$REPORT/openstack/error_server_$id.json" \
            2> "$REPORT/openstack/error_server_$id.stderr"
    done < "$REPORT/openstack/error_server_ids.txt"
    unset -f os_command
}

sql_rows() {
    timeout 60s "${MYSQL[@]}" --skip-column-names --execute "$1" 2>> "$REPORT/database/query_errors.txt"
}

sql_report() {
    local file=$1 sql=$2
    timeout 60s "${MYSQL[@]}" --execute "$sql" > "$REPORT/database/$file" 2> "$REPORT/database/$file.stderr"
}

collect_database() {
    local mysql_bin dump_bin schema table columns id url path state size
    printf 'NOT_STARTED\n' > "$REPORT/database/dump_status.txt"
    mysql_bin=$(command -v mariadb || command -v mysql || true)
    dump_bin=$(command -v mariadb-dump || command -v mysqldump || true)
    if [[ -z "$mysql_bin" ]]; then warn 'MariaDB client missing: full database export could not be performed.'; return; fi
    MYSQL=("$mysql_bin" --user=root --batch --raw --connect-timeout=10 --default-character-set=utf8mb4)
    if ! sql_rows 'SELECT 1' > "$REPORT/database/connection_test.txt"; then
        warn 'MariaDB root connection failed. No service was started or restarted; export is incomplete.'
        return
    fi
    sql_report server.tsv 'SELECT @@hostname AS host,@@version AS version,@@datadir AS datadir,@@socket AS socket,@@port AS port,@@read_only AS read_only;'
    sql_report databases.tsv 'SHOW DATABASES;'
    sql_report tables.tsv "SELECT TABLE_SCHEMA,TABLE_NAME,TABLE_TYPE,ENGINE,TABLE_ROWS,DATA_LENGTH,INDEX_LENGTH FROM information_schema.TABLES WHERE TABLE_SCHEMA NOT IN ('information_schema','performance_schema','sys') ORDER BY TABLE_SCHEMA,TABLE_NAME;"
    sql_report non_transactional_tables.tsv "SELECT TABLE_SCHEMA,TABLE_NAME,ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA NOT IN ('information_schema','performance_schema','sys') AND TABLE_TYPE='BASE TABLE' AND ENGINE <> 'InnoDB';"
    sql_report relevant_columns.tsv "SELECT TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA NOT IN ('information_schema','performance_schema','sys','mysql') AND (TABLE_NAME REGEXP 'image|device|target|sceneplugincloud.*element') ORDER BY TABLE_SCHEMA,TABLE_NAME,ORDINAL_POSITION;"

    # Export selected mapping columns for quick comparison; the SQL backup below has all rows/columns.
    while IFS=$'\t' read -r schema table; do
        [[ "$schema" =~ ^[a-zA-Z0-9_]+$ && "$table" =~ ^[a-zA-Z0-9_]+$ ]] || continue
        columns=$(sql_rows "SELECT GROUP_CONCAT(CONCAT(CHAR(96),COLUMN_NAME,CHAR(96)) ORDER BY ORDINAL_POSITION SEPARATOR ',') FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='$schema' AND TABLE_NAME='$table' AND COLUMN_NAME IN ('id','uuid','name','image','image_id','image_uuid','image_type','device_uuid','base_image_name','url','status','deleted','size','checksum','os_hash_algo','os_hash_value','disk_format','container_format','storage_id','resource_id','scene','scene_config','node_id','vendor','path','file_path','filename','created_at','updated_at','create_time','update_time');")
        [[ -n "$columns" && "$columns" != NULL ]] || continue
        sql_report "${schema}.${table}.sample.tsv" "SELECT $columns FROM \`$schema\`.\`$table\` LIMIT 10000;"
    done < <(sql_rows "SELECT TABLE_SCHEMA,TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA NOT IN ('information_schema','performance_schema','sys','mysql') AND TABLE_TYPE='BASE TABLE' AND (TABLE_NAME IN ('images','image_locations','image_properties','mp_devops_target','mp_sceneplugincloud_scene_element','mp_sceneplugincloud_scene_config_element') OR TABLE_NAME REGEXP '^device_.*(image|device)$') ORDER BY TABLE_SCHEMA,TABLE_NAME;")

    printf 'schema\timage_id\tstate_on_this_host\tsize_bytes\tlocation\n' > "$REPORT/database/glance_file_checks.tsv"
    while IFS= read -r schema; do
        [[ "$schema" =~ ^[a-zA-Z0-9_]+$ ]] || continue
        # Only inspect locations with the expected Glance columns, excluding deleted locations.
        columns=$(sql_rows "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='$schema' AND TABLE_NAME='image_locations' AND COLUMN_NAME IN ('image_id','url','deleted');")
        [[ "$columns" == 3 ]] || continue
        while IFS=$'\t' read -r id url; do
            state=NON_FILE_BACKEND; size=-
            if [[ "$url" == file:///* ]]; then
                path=${url#file://}
                IMAGE_DIRS["${path%/*}/"]=1
                state=MISSING_OR_NONLOCAL
                if [[ -f "$path" ]]; then
                    size=$(stat -Lc %s -- "$path" 2>/dev/null || printf '?')
                    state=PRESENT
                    [[ "$size" == 0 ]] && state=EMPTY
                fi
            fi
            printf '%s\t%s\t%s\t%s\t%s\n' "$schema" "$id" "$state" "$size" "$url" >> "$REPORT/database/glance_file_checks.tsv"
        done < <(sql_rows "SELECT image_id,url FROM \`$schema\`.image_locations WHERE deleted=0 LIMIT 10000;")
    done < <(sql_rows "SELECT TABLE_SCHEMA FROM information_schema.TABLES WHERE TABLE_NAME='image_locations' AND TABLE_SCHEMA NOT IN ('information_schema','performance_schema','sys','mysql');")

    if [[ -z "$dump_bin" ]]; then warn 'mariadb-dump / mysqldump missing: full database export could not be performed.'; return; fi
    say 'Exporting ALL local databases, tables, data, views, routines, triggers and events...'
    # No intermediate uncompressed SQL file. Keep full client errors and every pipeline exit status.
    "$dump_bin" --user=root --single-transaction --quick --hex-blob \
        --default-character-set=utf8mb4 --routines --triggers --events --all-databases \
        2> "$REPORT/database/dump.stderr" | gzip -1 -c | \
        split -b "$PART_BYTES" -d -a 6 - "$PARTS/all_databases.sql.gz.part"
    local -a statuses=("${PIPESTATUS[@]}")
    printf 'dump=%s gzip=%s split=%s\n' "${statuses[@]}" > "$REPORT/database/dump_status.txt"
    if [[ "${statuses[*]}" == '0 0 0' ]]; then
        BACKUP_OK=1
    else
        warn 'Database export FAILED. Any emitted SQL parts are named INCOMPLETE and are not a complete backup.'
        for path in "$PARTS"/all_databases.sql.gz.part*; do
            [[ -f "$path" ]] && mv -- "$path" "$PARTS/INCOMPLETE_${path##*/}"
        done
    fi
}

# Little-endian ZIP metadata. Already-compressed parts use ZIP method 0 (STORE).
# CRC32 is taken from gzip's trailer; no zip/unzip, Python, Perl or compiler is needed.
# Format reference: https://pkware.cachefly.net/webdocs/casestudies/APPNOTE.TXT
zip_uint() {
    local value=$1 count=$2 byte i
    for ((i=0; i<count; i++)); do
        printf -v byte '\\%03o' "$((value & 255))"
        printf '%b' "$byte"
        value=$((value >> 8))
    done
}

make_zip() {
    local source=$1 destination=$2 name size crc offset central_size
    local -a trailer
    name=${source##*/}; size=$(stat -c %s -- "$source") || return 1
    gzip -n -1 -c -- "$source" | tail -c 8 | od -An -v -t u1 > "$PARTS/crc.tmp" || return 1
    read -r -a trailer < "$PARTS/crc.tmp"
    [[ ${#trailer[@]} == 8 ]] || return 1
    crc=$((trailer[0] | trailer[1]<<8 | trailer[2]<<16 | trailer[3]<<24))
    offset=$((30 + ${#name} + size)); central_size=$((46 + ${#name}))
    {
        zip_uint 0x04034b50 4
        zip_uint 20 2; zip_uint 0 2; zip_uint 0 2; zip_uint 0 2; zip_uint 33 2
        zip_uint "$crc" 4; zip_uint "$size" 4; zip_uint "$size" 4
        zip_uint "${#name}" 2; zip_uint 0 2; printf '%s' "$name"
        cat -- "$source" || return 1
        zip_uint 0x02014b50 4
        zip_uint 20 2; zip_uint 20 2; zip_uint 0 2; zip_uint 0 2; zip_uint 0 2; zip_uint 33 2
        zip_uint "$crc" 4; zip_uint "$size" 4; zip_uint "$size" 4
        zip_uint "${#name}" 2
        zip_uint 0 2; zip_uint 0 2; zip_uint 0 2; zip_uint 0 2; zip_uint 0 4; zip_uint 0 4
        printf '%s' "$name"
        zip_uint 0x06054b50 4
        zip_uint 0 2; zip_uint 0 2; zip_uint 1 2; zip_uint 1 2
        zip_uint "$central_size" 4; zip_uint "$offset" 4; zip_uint 0 2
    } > "$destination" || return 1
    local actual_size
    actual_size=$(stat -c %s -- "$destination") || return 1
    [[ "$actual_size" -eq $((98 + 2 * ${#name} + size)) && "$actual_size" -le $ZIP_LIMIT ]]
}

package_results() {
    local part prefix hash size zip_file count=0
    cat > "$PACKAGES/README.txt" <<'EOF'
Return this entire packages directory, including EVERY ZIP and both checksum files.
Each ZIP is independently valid and strictly smaller than 10,000,000 bytes.
The pieces inside each ZIP must be reassembled; these are not .z01 multi-volume ZIPs.

On the analysis workstation, extract ALL ZIPs into the SAME empty directory.
Linux example (unzip is needed only on the analysis workstation):
  sha256sum -c SHA256SUMS
  for f in *.zip; do unzip -o "$f"; done
  cat diagnostics.tar.gz.part* > diagnostics.tar.gz
  cat all_databases.sql.gz.part* > all_databases.sql.gz
  sha256sum -c PAYLOAD_SHA256SUMS
  tar -xzf diagnostics.tar.gz
  gzip -dc all_databases.sql.gz > all_databases.sql

Extraction/reassembly does not import anything into a database.
Do not restore a full dump on a running production database for diagnostic analysis.
If INCOMPLETE SQL pieces exist, the full database export failed: see dump_status.txt.
Database dumps retain original data; diagnostic config/log redaction is best effort.
The report contains bounded samples; the successful SQL dump contains ALL databases.
--single-transaction is consistent for InnoDB; review non_transactional_tables.tsv.
File-location checks apply to the current host only. A missing file on a non-Glance
host does not prove loss on the actual storage server. No VM image binaries are copied.
EOF
    {
        printf 'Collector version: 2026-11-04\nHost: %s\nTime: %s\n' "$(hostname)" "$(date -Is)"
        printf 'Full local database export successful: %s\n' "$BACKUP_OK"
        printf 'cpcloud probe base URL: %s\n' "$CP_URL"
        printf 'Compare MP image/image_uuid, cpcloud target/image records, and Glance IDs/locations.\n'
        printf 'Review storage/, configs/, logs/, openstack/, database/ and warnings.txt.\n'
        if [[ -f "$REPORT/database/glance_file_checks.tsv" ]]; then
            printf '\nGlance location checks on THIS host:\n'
            awk -F '\t' 'NR>1 {count[$3]++} END {for (state in count) print state ": " count[state]}' \
                "$REPORT/database/glance_file_checks.tsv" | sort
        fi
        printf 'No services/mounts/images/database rows were modified.\n'
    } > "$REPORT/SUMMARY.txt"
    say 'Compressing diagnostic report...'
    tar -C "$REPORT" -czf - . | split -b "$PART_BYTES" -d -a 6 - "$PARTS/diagnostics.tar.gz.part"
    local -a statuses=("${PIPESTATUS[@]}")
    [[ "${statuses[*]}" == '0 0' ]] || { say 'FAIL: diagnostic archive creation failed; output retained.'; return 1; }
    : > "$PACKAGES/PAYLOAD_SHA256SUMS"
    for prefix in diagnostics.tar.gz all_databases.sql.gz INCOMPLETE_all_databases.sql.gz; do
        local -a pieces=("$PARTS/$prefix".part*)
        [[ -f "${pieces[0]}" ]] || continue
        hash=$(cat -- "${pieces[@]}" | sha256sum) || return 1
        printf '%s  %s\n' "${hash%% *}" "$prefix" >> "$PACKAGES/PAYLOAD_SHA256SUMS"
    done
    say 'Writing standalone ZIP parts (each at most 10 MB)...'
    for part in "$PARTS"/*.part*; do
        [[ -f "$part" ]] || continue
        zip_file="$PACKAGES/${OUT##*/}_${part##*/}.zip"
        if ! make_zip "$part" "$zip_file"; then
            say "FAIL: ZIP creation or size verification failed: ${part##*/}"
            return 1
        fi
        size=$(stat -c %s -- "$zip_file")
        say "${zip_file##*/} : $size bytes"
        rm -f -- "$part"
        ((count+=1))
    done
    (cd "$PACKAGES" && sha256sum -- *.zip > SHA256SUMS) || return 1
    rm -f -- "$PARTS/crc.tmp"
    rmdir -- "$PARTS" 2>/dev/null || true
    say "ZIP parts created: $count. Return directory: $PACKAGES"
}

main() {
    if [[ ${1:-} == --help || ${1:-} == -h ]]; then
        cat <<'EOF'
Usage (root): bash collect_image_diagnostics.sh [existing-output-parent-directory]
Default output parent: /root. One run collects diagnostics AND exports ALL local databases.
Offline: no installation, service restart, mount operation or database/image modification.
Required: Bash and standard Ubuntu core utilities; MariaDB client/dump for a complete backup.
Choose an output filesystem with room for the full compressed dump; 10 MB is a per-ZIP limit.
No dependency on zip/unzip, jq or Python. Existing OpenStack/Supervisor tools are used if present.
Optional environment: CPCLOUD_URL=http://host:11111 OPENSTACK_RC=/root/admin-openrc.sh
Exit 0: collection/packaging and full SQL export completed. Exit 2: SQL export incomplete.
Exit 1: collection/packaging could not complete. Review packages/README.txt for reassembly.
EOF
        return 0
    fi
    [[ $# -le 1 ]] || { printf 'Use --help for usage.\n' >&2; return 1; }
    [[ $EUID == 0 ]] || { printf 'Run this script as root.\n' >&2; return 1; }
    local tool parent=${1:-/root} host dir rc
    for tool in awk sed grep find sort head tail tr date hostname mkdir mktemp rm mv cat stat tar gzip split od sha256sum timeout readlink wc ps; do
        command -v "$tool" >/dev/null 2>&1 || { printf 'Required standard utility unavailable: %s\n' "$tool" >&2; return 1; }
    done
    [[ -d "$parent" ]] || { printf 'Output parent does not exist: %s\n' "$parent" >&2; return 1; }
    umask 077
    host=$(hostname -s | tr -cd 'a-zA-Z0-9_-')
    OUT=$(mktemp -d "${parent%/}/image_diagnostics_${host}_$(date +%Y%m%d_%H%M%S)_XXXXXX") || return 1
    REPORT="$OUT/report"; PACKAGES="$OUT/packages"; PARTS="$OUT/.parts"
    BACKUP_OK=0; CP_URL=${CPCLOUD_URL:-http://127.0.0.1:11111}; CP_URL=${CP_URL%/}
    declare -gA SEEN_CONFIGS=() IMAGE_DIRS=() LOG_FILES=()
    declare -ga MYSQL=()
    IMAGE_DIRS[/var/lib/glance/images]=1
    mkdir -p "$REPORT"/{system,storage,configs,processes,logs,network,openstack,database} "$PACKAGES" "$PARTS" || return 1
    : > "$REPORT/warnings.txt"
    trap 'printf "Interrupted. Partial output retained at %s\n" "$OUT" >&2; exit 130' INT TERM
    say "Output: $OUT"
    capture system/date.txt date -Is
    capture system/os.txt cat /etc/os-release
    capture system/kernel.txt uname -a
    capture system/uptime.txt uptime
    capture system/services.txt systemctl --no-pager --full status supervisor mariadb mysql glance-api nova-api nova-compute
    capture system/all_services.txt systemctl --no-pager --full list-units --all --type=service
    capture storage/disks.txt lsblk -o NAME,KNAME,TYPE,SIZE,FSTYPE,LABEL,UUID,MOUNTPOINT
    capture storage/filesystems.txt df -hT
    capture storage/output_filesystem.txt df -hT -- "$OUT"
    capture storage/inodes.txt df -i
    capture storage/mounts.txt findmnt --all -o TARGET,SOURCE,FSTYPE,OPTIONS,UUID
    capture storage/fstab_verify.txt findmnt --verify --verbose
    capture storage/blkid.txt blkid
    capture storage/mountinfo.txt cat /proc/self/mountinfo
    capture storage/mdstat.txt cat /proc/mdstat
    capture storage/disk_uuids.txt ls -l /dev/disk/by-uuid
    capture storage/kernel_storage_log.txt journalctl -k --since '7 days ago' -n 2000 --no-pager
    capture storage/pvs.txt pvs
    capture storage/vgs.txt vgs
    capture storage/lvs.txt lvs -a -o +devices
    capture storage/ceph_status.txt ceph -s
    capture storage/ceph_space.txt ceph df
    capture processes/supervisor.txt supervisorctl status
    capture logs/service_journal.txt journalctl -u supervisor -u glance-api -u nova-compute -u mariadb --since '3 days ago' -n 2000 --no-pager
    capture processes/docker.txt docker ps --no-trunc
    capture processes/docker_info.txt docker info
    capture processes/docker_images.txt docker images --digests
    capture network/addresses.txt ip -brief address
    capture network/routes.txt ip route
    capture network/listeners.txt ss -lntp
    say 'Collecting application configuration, process paths and recent logs...'
    collect_files_and_processes
    http_get "$CP_URL/cpcloud/device/v1/device/lists?page=1&page_size=200" 2>&1 | redact > "$REPORT/network/cpcloud_devices.txt"
    http_get 'http://127.0.0.1:9292/' 2>&1 | redact > "$REPORT/network/glance_versions.txt"
    say 'Collecting existing OpenStack CLI results...'
    collect_openstack
    say 'Collecting local database metadata and full export...'
    collect_database
    inspect_image_dirs
    package_results || return 1
    if [[ "$BACKUP_OK" != 1 ]]; then
        say 'INCOMPLETE: diagnostics were packaged, but the full database export failed; inspect report/database/.'
        return 2
    fi
    say 'COMPLETE: diagnostics and full local database export are ready.'
}

if [[ ${BASH_SOURCE[0]} == "$0" ]]; then main "$@"; fi
