drop table mp_situation_flow_port_protocol;

create table mp_situation_flow_port_protocol
(
    pk          bigint(8) auto_increment
        primary key,
    id          varchar(32)                          not null,
    port        int                                  not null,
    protocol    varchar(32)                          not null,
    create_time datetime default current_timestamp() not null,
    update_time datetime                             null
)
    comment '流量端口协议表';

INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (1, '1', 1, 'tcpmux
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (2, '2', 5, 'rje
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (3, '3', 7, 'echo
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (4, '4', 9, 'discard
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (5, '5', 11, 'systat
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (6, '6', 13, 'daytime
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (7, '7', 17, 'qotd
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (8, '8', 18, 'msp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (9, '9', 19, 'chargen
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (10, '10', 20, 'ftp-data
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (11, '11', 21, 'ftp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (12, '12', 22, 'ssh
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (13, '13', 23, 'telnet
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (14, '14', 24, 'any
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (15, '15', 25, 'smtp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (16, '16', 27, 'nsw-fe
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (17, '17', 29, 'msg-icp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (18, '18', 31, 'msg-auth
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (19, '19', 33, 'dsp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (20, '20', 35, 'any
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (21, '21', 37, 'time
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (22, '22', 38, 'rap
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (23, '23', 39, 'rlp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (24, '24', 41, 'graphics
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (25, '25', 42, 'nameserver
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (26, '26', 43, 'nicname
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (27, '27', 44, 'mpm-flags
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (28, '28', 45, 'mpm
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (29, '29', 46, 'mpm-snd
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (30, '30', 47, 'ni-ftp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (31, '31', 48, 'auditd
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (32, '32', 49, 'tacacs
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (33, '33', 50, 're-mail-ck
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (34, '34', 51, 'la-maint
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (35, '35', 52, 'xns-time
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (36, '36', 53, 'dns
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (37, '37', 54, 'xns-ch
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (38, '38', 55, 'isi-gl
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (39, '39', 56, 'xns-auth
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (40, '40', 57, 'any
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (41, '41', 58, 'xns-mail
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (42, '42', 59, 'any
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (43, '43', 60, 'Unassigned
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (44, '44', 61, 'ni-mail
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (45, '45', 62, 'acas
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (46, '46', 63, 'whois++
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (47, '47', 64, 'covia
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (48, '48', 65, 'tacacs-ds
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (49, '49', 66, 'sql*net
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (50, '50', 67, 'bootps
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (51, '51', 68, 'bootpc
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (52, '52', 69, 'tftp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (53, '53', 70, 'gopher
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (54, '54', 71, 'netrjs-1
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (55, '55', 72, 'netrjs-2
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (56, '56', 73, 'netrjs-3
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (57, '57', 74, 'netrjs-4
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (58, '58', 75, 'any
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (59, '59', 76, 'deos
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (60, '60', 77, 'any
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (61, '61', 78, 'vettcp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (62, '62', 79, 'finger
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (63, '63', 80, 'http
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (64, '64', 81, 'hosts2-ns
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (65, '65', 82, 'xfer
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (66, '66', 83, 'mit-ml-dev
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (67, '67', 84, 'ctf
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (68, '68', 85, 'mit-ml-dev
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (69, '69', 86, 'mfcobol
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (70, '70', 87, 'any
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (71, '71', 88, 'kerberos
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (72, '72', 89, 'su-mit-tg
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (73, '73', 90, 'dnsix
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (74, '74', 91, 'mit-dov
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (75, '75', 92, 'npp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (76, '76', 93, 'dcp
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (77, '77', 94, 'objcall
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (78, '78', 95, 'supdup
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (79, '79', 96, 'dixie
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (80, '80', 97, 'swift-rvf
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (81, '81', 98, 'tacnews
', '2022-02-28 07:10:28', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (82, '82', 99, 'metagram
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (83, '83', 101, 'hostname
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (84, '84', 102, 'iso-tsap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (85, '85', 103, 'gppitnp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (86, '86', 104, 'acr-nema
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (87, '87', 105, 'cso
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (88, '88', 105, 'csnet-ns
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (89, '89', 106, '3com-tsmux
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (90, '90', 107, 'rtelnet
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (91, '91', 108, 'snagas
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (92, '92', 109, 'pop2
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (93, '93', 110, 'pop3
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (94, '94', 111, 'sunrpc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (95, '95', 112, 'mcidas
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (96, '96', 113, 'ident 
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (97, '97', 114, 'audionews
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (98, '98', 115, 'sftp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (99, '99', 116, 'ansanotify
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (100, '100', 117, 'uucp-path
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (101, '101', 118, 'sqlserv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (102, '102', 119, 'nntp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (103, '103', 120, 'cfdptkt
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (104, '104', 121, 'erpc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (105, '105', 122, 'smakynet
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (106, '106', 123, 'ntp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (107, '107', 124, 'ansatrader
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (108, '108', 125, 'locus-map
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (109, '109', 126, 'unitary
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (110, '110', 127, 'locus-con
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (111, '111', 128, 'gss-xlicen
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (112, '112', 129, 'pwdgen
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (113, '113', 130, 'cisco-fna
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (114, '114', 131, 'cisco-tna
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (115, '115', 132, 'cisco-sys
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (116, '116', 133, 'statsrv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (117, '117', 134, 'ingres-net
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (118, '118', 135, 'epmap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (119, '119', 136, 'profile
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (120, '120', 137, 'netbios-ns
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (121, '121', 138, 'netbios-dgm
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (122, '122', 139, 'netbios-ssn
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (123, '123', 140, 'emfis-data
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (124, '124', 141, 'emfis-cntl
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (125, '125', 142, 'bl-idm
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (126, '126', 143, 'imap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (127, '127', 144, 'uma
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (128, '128', 145, 'uaac
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (129, '129', 146, 'iso-tp0
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (130, '130', 147, 'iso-ip
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (131, '131', 148, 'jargon
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (132, '132', 149, 'aed-512
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (133, '133', 150, 'sql-net
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (134, '134', 151, 'hems
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (135, '135', 152, 'bftp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (136, '136', 153, 'sgmp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (137, '137', 154, 'netsc-prod
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (138, '138', 155, 'netsc-dev
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (139, '139', 156, 'sqlsrv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (140, '140', 157, 'knet-cmp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (141, '141', 158, 'pcmail-srv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (142, '142', 159, 'nss-routing
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (143, '143', 160, 'sgmp-traps
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (144, '144', 161, 'snmp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (145, '145', 162, 'snmptrap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (146, '146', 163, 'cmip-man
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (147, '147', 164, 'cmip-agent
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (148, '148', 165, 'xns-courier
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (149, '149', 166, 's-net
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (150, '150', 167, 'namp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (151, '151', 168, 'rsvd
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (152, '152', 169, 'send
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (153, '153', 170, 'print-srv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (154, '154', 171, 'multiplex
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (155, '155', 172, 'cl/1
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (156, '156', 173, 'xyplex-mux
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (157, '157', 174, 'mailq
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (158, '158', 175, 'vmnet
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (159, '159', 176, 'genrad-mux
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (160, '160', 177, 'xdmcp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (161, '161', 178, 'nextstep
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (162, '162', 179, 'bgp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (163, '163', 180, 'ris
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (164, '164', 181, 'unify
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (165, '165', 182, 'audit
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (166, '166', 183, 'ocbinder
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (167, '167', 184, 'ocserver
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (168, '168', 185, 'remote-kis
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (169, '169', 186, 'kis
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (170, '170', 187, 'aci
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (171, '171', 188, 'mumps
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (172, '172', 189, 'qft
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (173, '173', 190, 'gacp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (174, '174', 191, 'prospero
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (175, '175', 192, 'osu-nms
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (176, '176', 193, 'srmp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (177, '177', 194, 'irc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (178, '178', 195, 'dn6-nlm-aud
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (179, '179', 196, 'dn6-smm-red
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (180, '180', 197, 'dls
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (181, '181', 198, 'dls-mon
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (182, '182', 199, 'smux
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (183, '183', 200, 'src
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (184, '184', 201, 'at-rtmp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (185, '185', 202, 'at-nbp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (186, '186', 203, 'at-3
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (187, '187', 204, 'at-echo
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (188, '188', 205, 'at-5
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (189, '189', 206, 'at-zis
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (190, '190', 207, 'at-7
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (191, '191', 208, 'at-8
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (192, '192', 209, 'qmtp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (193, '193', 210, 'z39.50
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (194, '194', 211, '914c/g
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (195, '195', 212, 'anet
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (196, '196', 214, 'vmpwscs
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (197, '197', 215, 'softpc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (198, '198', 216, 'CAIlic
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (199, '199', 217, 'dbase
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (200, '200', 218, 'mpp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (201, '201', 219, 'uarps
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (202, '202', 220, 'imap3
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (203, '203', 221, 'fln-spx
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (204, '204', 222, 'rsh-spx
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (205, '205', 223, 'cdc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (206, '206', 242, 'direct
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (207, '207', 243, 'sur-meas
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (208, '208', 244, 'dayna
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (209, '209', 245, 'link
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (210, '210', 246, 'dsp3270
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (211, '211', 247, 'subntbcst_tftp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (212, '212', 248, 'bhfhs
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (213, '213', 256, 'rap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (214, '214', 257, 'set
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (215, '215', 258, 'yak-chat
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (216, '216', 259, 'esro-gen
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (217, '217', 260, 'openport
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (218, '218', 263, 'hdap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (219, '219', 264, 'bgmp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (220, '220', 280, 'http-mgmt
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (221, '221', 309, 'entrusttime
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (222, '222', 310, 'bhmds
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (223, '223', 312, 'vslmp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (224, '224', 315, 'dpsi
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (225, '225', 316, 'decauth
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (226, '226', 317, 'zannet
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (227, '227', 321, 'pip
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (228, '228', 344, 'pdap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (229, '229', 345, 'pawserv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (230, '230', 346, 'zserv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (231, '231', 347, 'fatserv
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (232, '232', 348, 'csi-sgwp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (233, '233', 349, 'mftp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (234, '234', 351, 'matip-type-b
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (235, '235', 351, 'bhoetty
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (236, '236', 353, 'ndsauth
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (237, '237', 354, 'bh611
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (238, '238', 357, 'bhevent
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (239, '239', 362, 'srssend
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (240, '240', 365, 'dtk
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (241, '241', 366, 'odmr
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (242, '242', 368, 'qbikgdp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (243, '243', 371, 'clearcase
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (244, '244', 372, 'ulistproc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (245, '245', 373, 'legent-1
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (246, '246', 374, 'legent-2
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (247, '247', 375, 'hassle
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (248, '248', 376, 'nip
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (249, '249', 377, 'tnETOS
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (250, '250', 378, 'dsETOS
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (251, '251', 379, 'is99c
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (252, '252', 380, 'is99s
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (253, '253', 381, 'hp-collector
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (254, '254', 383, 'hp-alarm-mgr
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (255, '255', 384, 'arns
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (256, '256', 385, 'ibm-app
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (257, '257', 386, 'asa
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (258, '258', 387, 'aurp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (259, '259', 388, 'unidata-ldm
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (260, '260', 389, 'ldap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (261, '261', 390, 'uis
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (262, '262', 391, 'synotics-relay
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (263, '263', 393, 'dis
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (264, '264', 394, 'embl-ndt
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (265, '265', 395, 'netcp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (266, '266', 396, 'netware-ip
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (267, '267', 397, 'mptn
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (268, '268', 398, 'kryptolan
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (269, '269', 399, 'iso-tsap-c2
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (270, '270', 400, 'work-sol
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (271, '271', 401, 'ups
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (272, '272', 402, 'genie
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (273, '273', 403, 'decap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (274, '274', 404, 'nced
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (275, '275', 405, 'ncld
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (276, '276', 406, 'imsp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (277, '277', 407, 'timbuktu
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (278, '278', 408, 'prm-sm
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (279, '279', 409, 'prm-nm
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (280, '280', 410, 'decladebug
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (281, '281', 411, 'rmt
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (282, '282', 412, 'synoptics-trap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (283, '283', 413, 'smsp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (284, '284', 414, 'infoseek
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (285, '285', 415, 'bnet
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (286, '286', 416, 'silverplatter
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (287, '287', 417, 'onmux
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (288, '288', 418, 'hyper-g
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (289, '289', 419, 'ariel1
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (290, '290', 420, 'smpte
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (291, '291', 421, 'ariel2
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (292, '292', 422, 'ariel3
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (293, '293', 423, 'opc-job-start
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (294, '294', 424, 'opc-job-track
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (295, '295', 425, 'icad-el
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (296, '296', 426, 'smartsdp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (297, '297', 427, 'svrloc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (298, '298', 428, 'ocs_cmu
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (299, '299', 429, 'ocs_amu
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (300, '300', 430, 'utmpsd
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (301, '301', 431, 'utmpcd
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (302, '302', 432, 'iasd
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (303, '303', 433, 'nnsp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (304, '304', 434, 'mobileip-agent
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (305, '305', 435, 'mobilip-mn
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (306, '306', 436, 'dna-cml
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (307, '307', 437, 'comscm
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (308, '308', 438, 'dsfgw
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (309, '309', 439, 'dasp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (310, '310', 440, 'sgcp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (311, '311', 441, 'decvms-sysmgt
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (312, '312', 442, 'cvc_hostd
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (313, '313', 443, 'https
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (314, '314', 444, 'snpp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (315, '315', 445, 'microsoft-ds
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (316, '316', 446, 'ddm-rdb
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (317, '317', 447, 'ddm-dfm
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (318, '318', 448, 'ddm-ssl
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (319, '319', 449, 'as-servermap
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (320, '320', 450, 'tserver
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (321, '321', 451, 'sfs-smp-net
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (322, '322', 453, 'creativeserver
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (323, '323', 454, 'contentserver
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (324, '324', 455, 'creativepartnr
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (325, '325', 456, 'macon-tcp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (326, '326', 457, 'scohelp
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (327, '327', 458, 'appleqtc
', '2022-02-28 07:10:29', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (328, '328', 459, 'ampr-rcmd
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (329, '329', 460, 'skronk
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (330, '330', 461, 'datasurfsrv
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (331, '331', 462, 'datasurfsrvsec
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (332, '332', 463, 'alpes
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (333, '333', 464, 'kpasswd
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (334, '334', 465, 'smtps
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (335, '335', 466, 'digital-vrc
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (336, '336', 467, 'mylex-mapd
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (337, '337', 468, 'photuris
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (338, '338', 469, 'rcp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (339, '339', 470, 'scx-proxy
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (340, '340', 471, 'mondex
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (341, '341', 472, 'ljk-login
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (342, '342', 473, 'hybrid-pop
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (343, '343', 474, 'tn-tl-w1
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (344, '344', 475, 'tcpnethaspsrv
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (345, '345', 476, 'tn-tl-fd1
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (346, '346', 477, 'ss7ns
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (347, '347', 478, 'spsc
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (348, '348', 479, 'iafserver
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (349, '349', 480, 'iafdbase
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (350, '350', 481, 'ph
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (351, '351', 482, 'bgs-nsi
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (352, '352', 483, 'ulpnet
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (353, '353', 484, 'integra-sme
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (354, '354', 485, 'powerburst
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (355, '355', 486, 'avian
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (356, '356', 487, 'saft
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (357, '357', 488, 'gss-http
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (358, '358', 489, 'nest-protocol
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (359, '359', 490, 'micom-pfs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (360, '360', 491, 'go-login
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (361, '361', 492, 'ticf-1
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (362, '362', 493, 'ticf-2
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (363, '363', 494, 'pov-ray
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (364, '364', 495, 'intecourier
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (365, '365', 496, 'pim-rp-disc
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (366, '366', 497, 'dantz
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (367, '367', 498, 'siam
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (368, '368', 499, 'iso-ill
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (369, '369', 500, 'isakmp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (370, '370', 501, 'stmf
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (371, '371', 502, 'asa-appl-proto
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (372, '372', 503, 'intrinsa
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (373, '373', 504, 'citadel
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (374, '374', 505, 'mailbox-lm
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (375, '375', 506, 'ohimsrv
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (376, '376', 507, 'crs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (377, '377', 508, 'xvttp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (378, '378', 509, 'snare
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (379, '379', 510, 'fcp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (380, '380', 511, 'passgo
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (381, '381', 512, 'exec
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (382, '382', 513, 'login
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (383, '383', 514, 'shell
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (384, '384', 515, 'printer
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (385, '385', 516, 'videotex
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (386, '386', 517, 'talk
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (387, '387', 518, 'ntalk 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (388, '388', 519, 'utime
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (389, '389', 520, 'efs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (390, '390', 521, 'ripng
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (391, '391', 522, 'ulp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (392, '392', 523, 'ibm-db2
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (393, '393', 524, 'ncp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (394, '394', 525, 'timed
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (395, '395', 526, 'tempo
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (396, '396', 527, 'stx
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (397, '397', 528, 'custix
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (398, '398', 529, 'irc-serv
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (399, '399', 530, 'courier
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (400, '400', 531, 'conference
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (401, '401', 532, 'netnews
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (402, '402', 533, 'netwall
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (403, '403', 534, 'mm-admin
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (404, '404', 535, 'iiop
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (405, '405', 536, 'opalis-rdv
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (406, '406', 537, 'nmsp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (407, '407', 538, 'gdomap
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (408, '408', 539, 'apertus-ldp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (409, '409', 540, 'uucp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (410, '410', 541, 'uucp-rlogin
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (411, '411', 542, 'commerce
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (412, '412', 543, 'klogin 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (413, '413', 544, 'kshell
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (414, '414', 545, 'appleqtcsrvr
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (415, '415', 546, 'dhcpv6-client
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (416, '416', 547, 'dhcpv6-server
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (417, '417', 548, 'afpovertcp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (418, '418', 549, 'idfp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (419, '419', 550, 'new-rwho
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (420, '420', 551, 'cybercash
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (421, '421', 552, 'deviceshare
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (422, '422', 553, 'pirp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (423, '423', 554, 'rtsp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (424, '424', 555, 'dsf 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (425, '425', 556, 'remotefs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (426, '426', 557, 'openvms-sysipc
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (427, '427', 558, 'sdnskmp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (428, '428', 559, 'teedtap
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (429, '429', 560, 'rmonitor
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (430, '430', 561, 'monitor 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (431, '431', 562, 'chshell
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (432, '432', 563, 'nntps
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (433, '433', 564, '9pfs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (434, '434', 565, 'whoami
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (435, '435', 566, 'streettalk
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (436, '436', 567, 'banyan-rpc
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (437, '437', 568, 'ms-shuttle
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (438, '438', 569, 'ms-rome
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (439, '439', 570, 'meter
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (440, '440', 571, 'meter
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (441, '441', 572, 'sonar
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (442, '442', 573, 'banyan-vip
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (443, '443', 574, 'ftp-agent
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (444, '444', 575, 'vemmi
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (445, '445', 576, 'ipcd
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (446, '446', 577, 'vnas
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (447, '447', 578, 'ipdd
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (448, '448', 579, 'decbsrv
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (449, '449', 581, 'bdp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (450, '450', 588, 'cal
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (451, '451', 589, 'eyelink
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (452, '452', 590, 'tns-cml
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (453, '453', 593, 'http-rpc-epmap
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (454, '454', 594, 'tpip
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (455, '455', 596, 'smsd
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (456, '456', 599, 'acp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (457, '457', 600, 'ipcserver
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (458, '458', 606, 'urm
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (459, '459', 607, 'nqs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (460, '460', 608, 'sift-uft
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (461, '461', 609, 'npmp-trap
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (462, '462', 610, 'npmp-local
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (463, '463', 611, 'npmp-gui
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (464, '464', 613, 'hmmp-op
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (465, '465', 620, 'sco-websrvrmgr
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (466, '466', 621, 'escp-ip
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (467, '467', 625, 'dec_dlm
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (468, '468', 626, 'asia
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (469, '469', 628, 'qmqp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (470, '470', 630, 'rda
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (471, '471', 631, 'ipp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (472, '472', 632, 'bmpp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (473, '473', 634, 'ginad
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (474, '474', 635, 'rlzdbase
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (475, '475', 636, 'ldaps
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (476, '476', 637, 'lanserver
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (477, '477', 639, 'msdp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (478, '478', 666, 'doom
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (479, '479', 667, 'disclose
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (480, '480', 668, 'mecomm
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (481, '481', 669, 'meregister
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (482, '482', 670, 'vacdsm-sws
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (483, '483', 671, 'vacdsm-app
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (484, '484', 672, 'vpps-qua
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (485, '485', 673, 'cimplex
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (486, '486', 674, 'acap
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (487, '487', 675, 'dctp
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (488, '488', 704, 'elcsd
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (489, '489', 705, 'agentx
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (490, '490', 709, 'entrust-kmsh
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (491, '491', 710, 'entrust-ash
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (492, '492', 729, 'netviewdm1
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (493, '493', 730, 'netviewdm2
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (494, '494', 731, 'netviewdm3
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (495, '495', 741, 'netgw
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (496, '496', 742, 'netrcs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (497, '497', 744, 'flexlm
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (498, '498', 747, 'fujitsu-dev
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (499, '499', 748, 'ris-cm
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (500, '500', 749, 'kerberos-adm
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (501, '501', 750, 'rfile 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (502, '502', 751, 'pump 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (503, '503', 752, 'qrh 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (504, '504', 753, 'rrh 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (505, '505', 754, 'tell
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (506, '506', 758, 'nlogin 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (507, '507', 759, 'con 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (508, '508', 760, 'ns 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (509, '509', 761, 'rxe 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (510, '510', 762, 'quotad 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (511, '511', 763, 'cycleserv 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (512, '512', 764, 'omserv 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (513, '513', 765, 'webster 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (514, '514', 769, 'vid 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (515, '515', 770, 'cadlock 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (516, '516', 771, 'rtip 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (517, '517', 772, 'cycleserv2 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (518, '518', 773, 'submit 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (519, '519', 774, 'rpasswd 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (520, '520', 776, 'wpages 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (521, '521', 780, 'wpgs 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (522, '522', 786, 'concert
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (523, '523', 787, 'qsc
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (524, '524', 801, 'device 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (525, '525', 873, 'rsync
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (526, '526', 886, 'iclcnet-locate
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (527, '527', 887, 'iclcnet_svinfo
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (528, '528', 888, 'accessbuilder
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (529, '529', 900, 'omginitialrefs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (530, '530', 911, 'xact-backup
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (531, '531', 990, 'ftps
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (532, '532', 991, 'nas
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (533, '533', 992, 'telnets
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (534, '534', 993, 'imaps
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (535, '535', 994, 'ircs
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (536, '536', 995, 'pop3s
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (537, '537', 996, 'vsinet
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (538, '538', 997, 'maitrd 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (539, '539', 998, 'busboy 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (540, '540', 999, 'garcon 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (541, '541', 1000, 'cadlock 
', '2022-02-28 07:10:30', null);
INSERT INTO mp_situation_flow_port_protocol (pk, id, port, protocol, create_time, update_time) VALUES (542, '542', 1010, 'surf', '2022-02-28 07:10:30', null);
