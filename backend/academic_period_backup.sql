--
-- PostgreSQL database dump
--

\restrict 3GdwbLMEQrwNjQaWgE1VaxIj1mmjxni6yIllWvzOZbs9EU72ETRdhT94eNyb7pq

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: academic_period; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.academic_period (
    period_id integer NOT NULL,
    name character varying(100),
    start_date date,
    end_date date,
    active boolean
);


ALTER TABLE public.academic_period OWNER TO postgres;

--
-- Name: academic_period_period_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.academic_period_period_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.academic_period_period_id_seq OWNER TO postgres;

--
-- Name: academic_period_period_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.academic_period_period_id_seq OWNED BY public.academic_period.period_id;


--
-- Name: academic_period period_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academic_period ALTER COLUMN period_id SET DEFAULT nextval('public.academic_period_period_id_seq'::regclass);


--
-- Data for Name: academic_period; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.academic_period (period_id, name, start_date, end_date, active) FROM stdin;
\.


--
-- Name: academic_period_period_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.academic_period_period_id_seq', 1, false);


--
-- Name: academic_period academic_period_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academic_period
    ADD CONSTRAINT academic_period_pkey PRIMARY KEY (period_id);


--
-- PostgreSQL database dump complete
--

\unrestrict 3GdwbLMEQrwNjQaWgE1VaxIj1mmjxni6yIllWvzOZbs9EU72ETRdhT94eNyb7pq

