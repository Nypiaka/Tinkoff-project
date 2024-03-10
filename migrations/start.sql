create table if not exists links
(
	id bigserial primary key,
  link varchar unique
);

create table if not exists content
(
  link_id bigint references links (id) unique,
  content varchar
);

create table if not exists chats
(
  chat_id bigint,
  link_id bigint references links (id)
);
