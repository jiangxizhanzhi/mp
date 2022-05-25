INSERT INTO mp_auth_url_ac (id, url, is_pattern, methods, `desc`) VALUES ('001', '/api/mp/actuator/**', 1, 'get', '免认证接口');
INSERT INTO mp_auth_url_ac (id, url, is_pattern, methods, `desc`) VALUES ('002', '/api/mp/front/auth/urlac/evict_cache', 0, 'get', '免认证接口');
INSERT INTO mp_auth_url_ac (id, url, is_pattern, methods, `desc`) VALUES ('003', '/api/mp/sys/activation', 0, 'post', '免认证接口');
INSERT INTO mp_auth_url_ac (id, url, is_pattern, methods, `desc`) VALUES ('004', '/api/mp/sys/activation/**', 1, 'post', '免认证接口');
INSERT INTO mp_auth_url_ac_target (id, urlac_id, target) VALUES ('t001', '001', 'no_auth');
INSERT INTO mp_auth_url_ac_target (id, urlac_id, target) VALUES ('t002', '002', 'no_auth');
INSERT INTO mp_auth_url_ac_target (id, urlac_id, target) VALUES ('t003', '003', 'no_auth');
INSERT INTO mp_auth_url_ac_target (id, urlac_id, target) VALUES ('t004', '004', 'no_auth');
