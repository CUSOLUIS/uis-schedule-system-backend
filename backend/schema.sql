--
-- PostgreSQL database dump
--

\restrict geFyfu8pcYJ6AIJipSfGlDHs7tzSmESTaFSPG02MovcUXeCGXHp5A8jjU1IpSzp

-- Dumped from database version 14.19 (Ubuntu 14.19-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.19 (Ubuntu 14.19-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: academic_period_period_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.academic_period_period_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.academic_period_period_id_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: academic_period; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.academic_period (
    period_id bigint DEFAULT nextval('public.academic_period_period_id_seq'::regclass) NOT NULL,
    name character varying(100),
    start_date date,
    end_date date,
    active boolean
);


ALTER TABLE public.academic_period OWNER TO postgres;

--
-- Name: audit_log_audit_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.audit_log_audit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_log_audit_id_seq OWNER TO postgres;

--
-- Name: audit_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.audit_log (
    audit_id bigint DEFAULT nextval('public.audit_log_audit_id_seq'::regclass) NOT NULL,
    action character varying(250),
    module character varying(250),
    date date,
    origin_ip character varying(250),
    user_id bigint,
    start_date date
);


ALTER TABLE public.audit_log OWNER TO postgres;

--
-- Name: class; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.class (
    class_id integer NOT NULL,
    user_id integer,
    group_id integer,
    class_type character varying(10)
);


ALTER TABLE public.class OWNER TO postgres;

--
-- Name: class_class_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.class_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.class_class_id_seq OWNER TO postgres;

--
-- Name: class_class_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.class_class_id_seq OWNED BY public.class.class_id;


--
-- Name: class_hour; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.class_hour (
    class_hour_id integer NOT NULL,
    day integer,
    hour time without time zone,
    group_id integer,
    classroom_id integer
);


ALTER TABLE public.class_hour OWNER TO postgres;

--
-- Name: class_hour_class_hour_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.class_hour_class_hour_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.class_hour_class_hour_id_seq OWNER TO postgres;

--
-- Name: class_hour_class_hour_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.class_hour_class_hour_id_seq OWNED BY public.class_hour.class_hour_id;


--
-- Name: classroom; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.classroom (
    classroom_id integer NOT NULL,
    number integer,
    max_capacity integer,
    building character varying(250),
    campus character varying(100),
    type character varying(100)
);


ALTER TABLE public.classroom OWNER TO postgres;

--
-- Name: classroom_classroom_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.classroom_classroom_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.classroom_classroom_id_seq OWNER TO postgres;

--
-- Name: classroom_classroom_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.classroom_classroom_id_seq OWNED BY public.classroom.classroom_id;


--
-- Name: day_of_week; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.day_of_week (
    day_id integer NOT NULL,
    name character varying(16)
);


ALTER TABLE public.day_of_week OWNER TO postgres;

--
-- Name: day_of_week_day_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.day_of_week_day_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.day_of_week_day_id_seq OWNER TO postgres;

--
-- Name: day_of_week_day_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.day_of_week_day_id_seq OWNED BY public.day_of_week.day_id;


--
-- Name: faculty; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.faculty (
    faculty_id integer NOT NULL,
    name character varying(100)
);


ALTER TABLE public.faculty OWNER TO postgres;

--
-- Name: faculty_faculty_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.faculty_faculty_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.faculty_faculty_id_seq OWNER TO postgres;

--
-- Name: faculty_faculty_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.faculty_faculty_id_seq OWNED BY public.faculty.faculty_id;


--
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.groups (
    group_id integer NOT NULL,
    group_name character varying(250),
    capacity integer,
    teacher_id integer,
    period_id integer,
    subject_id integer
);


ALTER TABLE public.groups OWNER TO postgres;

--
-- Name: groups_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.groups_group_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.groups_group_id_seq OWNER TO postgres;

--
-- Name: groups_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.groups_group_id_seq OWNED BY public.groups.group_id;


--
-- Name: permissions; Type: TABLE; Schema: public; Owner: schedule_user
--

CREATE TABLE public.permissions (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.permissions OWNER TO schedule_user;

--
-- Name: permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: schedule_user
--

ALTER TABLE public.permissions ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: role_permissions; Type: TABLE; Schema: public; Owner: schedule_user
--

CREATE TABLE public.role_permissions (
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL
);


ALTER TABLE public.role_permissions OWNER TO schedule_user;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    roles_id integer NOT NULL,
    rol character varying(32) NOT NULL
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- Name: roles_roles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.roles_roles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_roles_id_seq OWNER TO postgres;

--
-- Name: roles_roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.roles_roles_id_seq OWNED BY public.roles.roles_id;


--
-- Name: schedule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule (
    schedule_id integer NOT NULL,
    class_id integer
);


ALTER TABLE public.schedule OWNER TO postgres;

--
-- Name: schedule_schedule_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_schedule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.schedule_schedule_id_seq OWNER TO postgres;

--
-- Name: schedule_schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_schedule_id_seq OWNED BY public.schedule.schedule_id;


--
-- Name: school; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.school (
    school_id integer NOT NULL,
    faculty_id integer,
    name character varying(100)
);


ALTER TABLE public.school OWNER TO postgres;

--
-- Name: school_school_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.school_school_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.school_school_id_seq OWNER TO postgres;

--
-- Name: school_school_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.school_school_id_seq OWNED BY public.school.school_id;


--
-- Name: subject; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.subject (
    subject_id integer NOT NULL,
    code character varying(250),
    name character varying(250),
    credits integer,
    theory_hours integer,
    practice_hours integer,
    school_id integer
);


ALTER TABLE public.subject OWNER TO postgres;

--
-- Name: subject_subject_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.subject_subject_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.subject_subject_id_seq OWNER TO postgres;

--
-- Name: subject_subject_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.subject_subject_id_seq OWNED BY public.subject.subject_id;


--
-- Name: teacher; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.teacher (
    teacher_id integer NOT NULL,
    availability character varying(250),
    department character varying(250),
    user_id integer
);


ALTER TABLE public.teacher OWNER TO postgres;

--
-- Name: teacher_teacher_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.teacher_teacher_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.teacher_teacher_id_seq OWNER TO postgres;

--
-- Name: teacher_teacher_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.teacher_teacher_id_seq OWNED BY public.teacher.teacher_id;


--
-- Name: user_rol; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_rol (
    user_rol_id integer NOT NULL,
    user_id integer NOT NULL,
    roles_id integer NOT NULL
);


ALTER TABLE public.user_rol OWNER TO postgres;

--
-- Name: user_rol_user_rol_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_rol_user_rol_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_rol_user_rol_id_seq OWNER TO postgres;

--
-- Name: user_rol_user_rol_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_rol_user_rol_id_seq OWNED BY public.user_rol.user_rol_id;


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: schedule_user
--

CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.user_roles OWNER TO schedule_user;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    email character varying(250) NOT NULL,
    password character varying(100) NOT NULL,
    name character varying(20),
    permissions text,
    active boolean,
    last_session date,
    schedule_id integer
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_user_id_seq OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- Name: class class_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class ALTER COLUMN class_id SET DEFAULT nextval('public.class_class_id_seq'::regclass);


--
-- Name: class_hour class_hour_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class_hour ALTER COLUMN class_hour_id SET DEFAULT nextval('public.class_hour_class_hour_id_seq'::regclass);


--
-- Name: classroom classroom_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.classroom ALTER COLUMN classroom_id SET DEFAULT nextval('public.classroom_classroom_id_seq'::regclass);


--
-- Name: day_of_week day_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.day_of_week ALTER COLUMN day_id SET DEFAULT nextval('public.day_of_week_day_id_seq'::regclass);


--
-- Name: faculty faculty_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.faculty ALTER COLUMN faculty_id SET DEFAULT nextval('public.faculty_faculty_id_seq'::regclass);


--
-- Name: groups group_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups ALTER COLUMN group_id SET DEFAULT nextval('public.groups_group_id_seq'::regclass);


--
-- Name: roles roles_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles ALTER COLUMN roles_id SET DEFAULT nextval('public.roles_roles_id_seq'::regclass);


--
-- Name: schedule schedule_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule ALTER COLUMN schedule_id SET DEFAULT nextval('public.schedule_schedule_id_seq'::regclass);


--
-- Name: school school_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.school ALTER COLUMN school_id SET DEFAULT nextval('public.school_school_id_seq'::regclass);


--
-- Name: subject subject_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subject ALTER COLUMN subject_id SET DEFAULT nextval('public.subject_subject_id_seq'::regclass);


--
-- Name: teacher teacher_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teacher ALTER COLUMN teacher_id SET DEFAULT nextval('public.teacher_teacher_id_seq'::regclass);


--
-- Name: user_rol user_rol_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_rol ALTER COLUMN user_rol_id SET DEFAULT nextval('public.user_rol_user_rol_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- Name: academic_period academic_period_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academic_period
    ADD CONSTRAINT academic_period_pkey PRIMARY KEY (period_id);


--
-- Name: audit_log audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (audit_id);


--
-- Name: class_hour class_hour_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class_hour
    ADD CONSTRAINT class_hour_pkey PRIMARY KEY (class_hour_id);


--
-- Name: class class_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT class_pkey PRIMARY KEY (class_id);


--
-- Name: classroom classroom_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.classroom
    ADD CONSTRAINT classroom_pkey PRIMARY KEY (classroom_id);


--
-- Name: day_of_week day_of_week_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.day_of_week
    ADD CONSTRAINT day_of_week_pkey PRIMARY KEY (day_id);


--
-- Name: faculty faculty_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.faculty
    ADD CONSTRAINT faculty_pkey PRIMARY KEY (faculty_id);


--
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (group_id);


--
-- Name: permissions permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: schedule_user
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- Name: role_permissions role_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: schedule_user
--

ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT role_permissions_pkey PRIMARY KEY (role_id, permission_id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (roles_id);


--
-- Name: schedule schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (schedule_id);


--
-- Name: school school_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.school
    ADD CONSTRAINT school_pkey PRIMARY KEY (school_id);


--
-- Name: subject subject_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subject
    ADD CONSTRAINT subject_pkey PRIMARY KEY (subject_id);


--
-- Name: teacher teacher_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (teacher_id);


--
-- Name: permissions ukpnvtwliis6p05pn6i3ndjrqt2; Type: CONSTRAINT; Schema: public; Owner: schedule_user
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT ukpnvtwliis6p05pn6i3ndjrqt2 UNIQUE (name);


--
-- Name: users uq_user_schedule; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uq_user_schedule UNIQUE (schedule_id);


--
-- Name: user_rol user_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_rol
    ADD CONSTRAINT user_rol_pkey PRIMARY KEY (user_rol_id);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: schedule_user
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_password_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_password_key UNIQUE (password);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: users users_schedule_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_schedule_id_key UNIQUE (schedule_id);


--
-- Name: audit_log fk_audit_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: class fk_class_group; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT fk_class_group FOREIGN KEY (group_id) REFERENCES public.groups(group_id);


--
-- Name: class_hour fk_class_hour_classroom; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class_hour
    ADD CONSTRAINT fk_class_hour_classroom FOREIGN KEY (classroom_id) REFERENCES public.classroom(classroom_id);


--
-- Name: class_hour fk_class_hour_day; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class_hour
    ADD CONSTRAINT fk_class_hour_day FOREIGN KEY (day) REFERENCES public.day_of_week(day_id);


--
-- Name: class_hour fk_class_hour_group; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class_hour
    ADD CONSTRAINT fk_class_hour_group FOREIGN KEY (group_id) REFERENCES public.groups(group_id);


--
-- Name: class fk_class_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT fk_class_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: groups fk_group_period; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT fk_group_period FOREIGN KEY (period_id) REFERENCES public.academic_period(period_id);


--
-- Name: groups fk_group_subject; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT fk_group_subject FOREIGN KEY (subject_id) REFERENCES public.subject(subject_id);


--
-- Name: groups fk_group_teacher; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT fk_group_teacher FOREIGN KEY (teacher_id) REFERENCES public.teacher(teacher_id);


--
-- Name: schedule fk_schedule_class; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT fk_schedule_class FOREIGN KEY (class_id) REFERENCES public.class(class_id);


--
-- Name: school fk_subject_faculty; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.school
    ADD CONSTRAINT fk_subject_faculty FOREIGN KEY (faculty_id) REFERENCES public.faculty(faculty_id);


--
-- Name: subject fk_subject_school; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subject
    ADD CONSTRAINT fk_subject_school FOREIGN KEY (school_id) REFERENCES public.school(school_id);


--
-- Name: teacher fk_teacher_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teacher
    ADD CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: users fk_user_schedule; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_user_schedule FOREIGN KEY (schedule_id) REFERENCES public.schedule(schedule_id);


--
-- Name: user_rol user_rol_roles_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_rol
    ADD CONSTRAINT user_rol_roles_id_fkey FOREIGN KEY (roles_id) REFERENCES public.roles(roles_id);


--
-- Name: user_rol user_rol_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_rol
    ADD CONSTRAINT user_rol_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA public TO schedule_user;


--
-- PostgreSQL database dump complete
--

\unrestrict geFyfu8pcYJ6AIJipSfGlDHs7tzSmESTaFSPG02MovcUXeCGXHp5A8jjU1IpSzp

