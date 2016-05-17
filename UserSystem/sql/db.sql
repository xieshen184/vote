create sequence seq_profile;
drop sequence seq_profile;
drop teble profile;

create table profile(
     id number primary key,
     name varchar2(20 char) not null, 
     birthday varchar2(20 char)not null, 
     gender varchar2(4 char), 
     career varchar2(20 char), 
     address varchar2(50 char), 
     mobile varchar2(20 char)
);

insert into profile values(seq_profile.nextval,'张三','1982-07-07','男','程序员','湖南','1234566');
insert into profile values(seq_profile.nextval,'张四','1982-07-07','男','程序员','湖南','1234566');
insert into profile values(seq_profile.nextval,'张五','1982-07-07','男','程序员','湖南','1234566');
insert into profile values(seq_profile.nextval,'张六','1982-07-07','男','程序员','湖南','1234566');


select * from profile;


alter table profile
add constraint uk_profile_name unique(name);