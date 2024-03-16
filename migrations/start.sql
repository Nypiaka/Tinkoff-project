create table if not exists links
(
	id bigserial primary key,
  link varchar unique
);

create table if not exists content
(
  link_id bigint references links (id),
  content varchar,
  updated_at timestamp,
  unique(link_id)
);

create table if not exists chats
(
  chat_id bigint,
  link_id bigint references links (id),
  unique (chat_id, link_id)
);
