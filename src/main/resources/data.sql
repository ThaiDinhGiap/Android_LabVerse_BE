-- Roles
INSERT INTO roles (role_name, delete_flag) VALUES (N'PI', FALSE);
INSERT INTO roles (role_name, delete_flag) VALUES (N'RESEARCHER', FALSE);
INSERT INTO roles (role_name, delete_flag) VALUES (N'STUDENT', FALSE);

-- Users - Password là "123"
INSERT INTO users (
    email, username, password, phone_number, full_name, avatar_url,
    role_id, enabled, push_notifications, email_notifications,
    fcm_token, delete_flag
)
VALUES
    ('pi@gmail.com', 'pi_lead', '$2a$12$ShPV6hcFmFJGiJ0H9f78CeIyNHmz42MclkvgN60VVhaCV.OlcOKba',
     '0901000001', N'Lê Minh Quân', 'https://i.pinimg.com/originals/7b/1e/b9/7b1eb944285fe8822ebe3fc0a036e1f9.png',
     1, TRUE, TRUE, TRUE,
     'ctGH7JHMQw2l3qYL-Mw6SI:APA91bFdp0pL-2giW8n_1jvSmWAMbkTvskTLsaXnjbH8z7ctyco3J0osSRlYoR7cGIs07Yi4zLiazdlLOAUoDBNWPLUzlJGbL4pZczBeRY1pfd3pHNIgcGY',
     FALSE),

    ('st1@gmail.com', 'student_01', '$2a$12$ShPV6hcFmFJGiJ0H9f78CeIyNHmz42MclkvgN60VVhaCV.OlcOKba',
     '0902000001', N'Nguyễn Thị Hoa', 'https://i.pinimg.com/originals/7b/1e/b9/7b1eb944285fe8822ebe3fc0a036e1f9.png',
     3, TRUE, TRUE, TRUE,
     'ctGH7JHMQw2l3qYL-Mw6SI:APA91bFdp0pL-2giW8n_1jvSmWAMbkTvskTLsaXnjbH8z7ctyco3J0osSRlYoR7cGIs07Yi4zLiazdlLOAUoDBNWPLUzlJGbL4pZczBeRY1pfd3pHNIgcGY',
     FALSE),

    ('st2@gmail.com', 'student_02', '$2a$12$ShPV6hcFmFJGiJ0H9f78CeIyNHmz42MclkvgN60VVhaCV.OlcOKba',
     '0902000002', N'Phạm Văn Bình', 'https://i.pinimg.com/originals/7b/1e/b9/7b1eb944285fe8822ebe3fc0a036e1f9.png',
     3, TRUE, TRUE, TRUE,
     'ctGH7JHMQw2l3qYL-Mw6SI:APA91bFdp0pL-2giW8n_1jvSmWAMbkTvskTLsaXnjbH8z7ctyco3J0osSRlYoR7cGIs07Yi4zLiazdlLOAUoDBNWPLUzlJGbL4pZczBeRY1pfd3pHNIgcGY',
     FALSE),

    ('rs1@gmail.com', 'researcher_01', '$2a$12$ShPV6hcFmFJGiJ0H9f78CeIyNHmz42MclkvgN60VVhaCV.OlcOKba',
     '0903000001', N'Trần Hải Nam', 'https://i.pinimg.com/originals/7b/1e/b9/7b1eb944285fe8822ebe3fc0a036e1f9.png',
     2, TRUE, TRUE, TRUE,
     'ctGH7JHMQw2l3qYL-Mw6SI:APA91bFdp0pL-2giW8n_1jvSmWAMbkTvskTLsaXnjbH8z7ctyco3J0osSRlYoR7cGIs07Yi4zLiazdlLOAUoDBNWPLUzlJGbL4pZczBeRY1pfd3pHNIgcGY',
     FALSE),

    ('rs2@gmail.com', 'researcher_02', '$2a$12$ShPV6hcFmFJGiJ0H9f78CeIyNHmz42MclkvgN60VVhaCV.OlcOKba',
     '0903000002', N'Vũ Thị Lan', 'https://i.pinimg.com/originals/7b/1e/b9/7b1eb944285fe8822ebe3fc0a036e1f9.png',
     2, TRUE, TRUE, TRUE,
     'ctGH7JHMQw2l3qYL-Mw6SI:APA91bFdp0pL-2giW8n_1jvSmWAMbkTvskTLsaXnjbH8z7ctyco3J0osSRlYoR7cGIs07Yi4zLiazdlLOAUoDBNWPLUzlJGbL4pZczBeRY1pfd3pHNIgcGY',
     FALSE),

    ('rs3@gmail.com', 'researcher_03', '$2a$12$ShPV6hcFmFJGiJ0H9f78CeIyNHmz42MclkvgN60VVhaCV.OlcOKba',
     '0903000003', N'Hoàng Đức Tài', 'https://i.pinimg.com/originals/7b/1e/b9/7b1eb944285fe8822ebe3fc0a036e1f9.png',
     2, TRUE, TRUE, TRUE,
     'ctGH7JHMQw2l3qYL-Mw6SI:APA91bFdp0pL-2giW8n_1jvSmWAMbkTvskTLsaXnjbH8z7ctyco3J0osSRlYoR7cGIs07Yi4zLiazdlLOAUoDBNWPLUzlJGbL4pZczBeRY1pfd3pHNIgcGY',
     FALSE);
