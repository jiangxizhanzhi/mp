mkdir -p /home/apps
cd /home/apps
curl -o logstash.tar.gz ${logstash_install_resource_url}
tar -zxvf logstash.tar.gz
cd /home/apps/logstash
sed -i 's/{server_ip}/${server_ip_value}/g' ./pipeline/snmptrap.conf
sed -i 's/{server_port}/${server_port_value}/g' ./pipeline/snmptrap.conf
sed -i 's/{server_ip}/${server_ip_value}/g' ./pipeline/syslog.conf
cd /home/apps/logstash/pipeline
sed '1s/^\xEF\xBB\xBF//' snmptrap.conf > snmptrap.conf2
sed '1s/^\xEF\xBB\xBF//' syslog.conf > syslog.conf2
mv snmptrap.conf2 snmptrap.conf
mv syslog.conf2 syslog.conf
cd /home/apps/logstash
curl -o map.yml ${logstash_install_map_yml_url}
mv map.yml ./conf/map.yml
docker-compose up -d