INSERT INTO users (
    last_name,
    first_name,
    middle_name,
    birth_date,
    passport_series,
    passport_number
) VALUES (
    'Иванов',
    'Иван',
    'Иванович',
    DATE '1999-05-14',
    '1234',
    '567890'
);


INSERT INTO bank (
    card_number,
    cvc,
    balance
) VALUES (
    '1111222233334444',
    '123',
    15000.00
);


INSERT INTO user_data (
    account_number,
    phone_number,
    balance,
    remaining_seconds,
    remaining_bytes,
    remaining_sms,
    has_promised_payment,
    promised_payment_amount,
    promised_payment_due_date,
    is_blocked,
    user_id
) VALUES (
    'ACC1000001',
    '79161234567',
    1200.00,
    500,
    10737418240, -- 10 ГБ
    100,
    FALSE,
    0,
    NULL,
    FALSE,
    (SELECT id
     FROM users
     WHERE passport_series = '1234'
       AND passport_number = '567890')
);

INSERT INTO money_operations (
    operation_time,
    op_type,
    op_name,
    amount,
    user_data_id
) VALUES
(
    '2026-02-10 10:15:00',
    'INCOME',
    'Регистрация платежа: SberPay',
    1500.00,
    (SELECT id FROM user_data WHERE phone_number = '79161234567')
),
(
    '2026-02-12 14:40:00',
    'EXPENSE',
    'Ежемесячная плата услуги Х',
    300.00,
    (SELECT id FROM user_data WHERE phone_number = '79161234567')
),
(
    '2026-02-15 09:00:00',
    'EXPENSE',
    'Покупка услуги Х',
    100.00,
    (SELECT id FROM user_data WHERE phone_number = '79161234567')
);


INSERT INTO service_usage (
    operation_type,
    direction,
    name,
    units_used,
    operation_time,
    user_data_id
) VALUES
(
    'CALL',
    'OUTGOING',
    'Исходящий звонок',
    35,
    '2026-02-11 12:00:00',
    (SELECT id FROM user_data WHERE phone_number = '79161234567')
),
(
    'SMS',
    'INCOMING',
    'Входящее СМС',
 0,
    '2026-02-11 13:30:00',
    (SELECT id FROM user_data WHERE phone_number = '79161234567')
),
(
    'INTERNET',
    NULL,
    NULL,
    2048,
    '2026-02-12 18:20:00',
    (SELECT id FROM user_data WHERE phone_number = '79161234567')
);
