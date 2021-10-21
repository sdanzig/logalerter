SET search_path TO main,public;

INSERT INTO tbl_log (description, log_type_id) VALUES
  ('Application log', 1),
  ('Postgres log', 1),
  ('CloudTrail audit log', 1),
  ('Data Export Service log', 1),
  ('CodeBuild log', 1)
;

INSERT INTO tbl_alert (label, regex, log_id, severity) VALUES
  ('Error creating bean', 'Error creating bean with name', 1, 2),
  ('API Gateway down', 'Cannot connect to server', 1, 2),
  ('Out of space', 'Cannot write', 4, 1),
  ('Invalid token', 'Cannot send [A-Za-z0-9]+ request to remote server', 1, 0),
  ('JiBX error', 'Caused by: .*\\build.xml:262: JiBXException in JiBX binding compilation', 5, 1)
;

INSERT INTO tbl_alert_update (change_type, alert_id, log_id) VALUES
  ('ADD', 1, 1),
  ('ADD', 2, 1),
  ('ADD', 3, 4),
  ('ADD', 4, 1),
  ('ADD', 5, 5)
;

INSERT INTO tbl_note (email, info, alert_id) VALUES
  ('koala.bear@example.com', 'This is probably because your database is out of sync. Rebuild your database with the ant database-build task.', 1),
  ('koala.bear@example.com', 'The API token has expired. JIRA OC-349242', 4),
  ('joe.peterson@example.com', 'Check your server.xml is updated as per dev setup wiki.', 2),
  ('bobo.monkey@example.com', 'Delete temp files off server.', 3)
;

INSERT INTO tbl_user (email, full_name, contact_info) VALUES
  ('koala.bear@example.com', 'Pete Koala', 'koala.bear@example.com'),
  ('joe.peterson@example.com', 'Joe Peterson', '617-555-1212'),
  ('bobo.monkey@example.com', 'John Brown', 'bobo.monkey@example.com'),
  ('nothingtoseehere@example.com', 'Jenny Goodman', '867-5309')
;