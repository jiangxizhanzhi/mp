INSERT INTO mp_auth_url_ac (id, url, is_pattern, methods, `desc`) VALUES ('0000', '/api/mp/actuator/**', 1, 'get', '免认证接口');
INSERT INTO mp_auth_url_ac_target (id, urlac_id, target) VALUES ('t0000', '0000', 'no_auth');
