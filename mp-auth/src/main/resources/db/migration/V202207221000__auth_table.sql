CREATE INDEX idx_ol_rec_user_etime on mp_auth_user_online_record(user, end_time);
CREATE INDEX idx_orgu_uorg on mp_auth_org_user(user, org);
CREATE INDEX idx_orgu_orgu on mp_auth_org_user(org, user);

CREATE INDEX idx_userrole_ur on mp_auth_user_role(user, role);
CREATE INDEX idx_userrole_ru on mp_auth_user_role(role, user);
