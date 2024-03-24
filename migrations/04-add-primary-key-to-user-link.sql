alter table user_link
    add constraint unique_user_link
        unique (user_id, link_id);
