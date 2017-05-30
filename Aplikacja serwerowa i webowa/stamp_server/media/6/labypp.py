from urllib.parse import quote_plus
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine, Column, Integer, String, ForeignKey
from sqlalchemy.orm import sessionmaker, relationship, backref
import pyodbc

Base = declarative_base()


class Klient(Base):
    __tablename__ = 'klient'
    id = Column(Integer, primary_key=True)
    imie = Column(String)
    nazwisko = Column(String)

    adresy = relationship("Adres", back_populates="klient_powiazanie")
    emaile = relationship("Email", back_populates="klient_powiazanie")
    telefony = relationship("Telefon", back_populates="klient_powiazanie")
    
    friends = relationship('OsobyPowiazane', backref='OsobyPowiazane.druga',primaryjoin='Klient.id==OsobyPowiazane.pierwsza')

    def __init__(self, imie, nazwisko):
        self.imie = imie
        self.nazwisko = nazwisko

class Adres(Base):
    __tablename__='adres'
    id = Column(Integer, primary_key=True)
    klient = Column(Integer, ForeignKey('klient.id'))
    ulica = Column(String)
    miasto = Column(String)
    kod_pocztowy = Column(String)

    klient_powiazanie = relationship("Klient", back_populates="adresy")

    def __init__(self, klient, ulica, miasto, kod_pocztowy):
        self.klient = klient
        self.ulica = ulica
        self.miasto = miasto
        self.kod_pocztowy = kod_pocztowy

class Email(Base):
    __tablename__='email'
    id = Column(Integer, primary_key=True)
    klient = Column(Integer, ForeignKey('klient.id'), nullable=False)
    email = Column(String)

    klient_powiazanie = relationship("Klient", back_populates="emaile")

    def __init__(self, klient, email):
        self.klient = klient
        self.email = email

class Telefon(Base):
    __tablename__='telefon'
    id = Column(Integer, primary_key=True)
    klient = Column(Integer, ForeignKey('klient.id'), nullable=False)
    telefon = Column(String)

    klient_powiazanie = relationship("Klient", back_populates="telefony")

    def __init__(self, klient, telefon):
        self.klient = klient
        self.telefon = telefon

class OsobyPowiazane(Base):
    __tablename__='osobypowiazane'
    id = Column(Integer, primary_key=True)
    pierwsza = Column(Integer, ForeignKey('klient.id'), nullable=False)
    druga = Column(Integer, ForeignKey('klient.id'), nullable=False)

    pierwsza_powiazanie = relationship('Klient', foreign_keys='OsobyPowiazane.pierwsza')
    druga_powiazanie = relationship('Klient', foreign_keys='OsobyPowiazane.druga')

    def __init__(self, pierwsza, druga):
        self.pierwsza = pierwsza
        self.druga = druga

# connection string
connection_string = "DRIVER={SQL Server};SERVER=.\\MACIEJ_HP;DATABASE=MMDB;UID=MM;PWD=maciej"
connection_string = quote_plus(connection_string) 
connection_string = "mssql+pyodbc:///?odbc_connect=%s" % connection_string

# polaczenie
engine = create_engine(connection_string, echo=False)
connection = engine.connect()

if engine.has_table("Klient"):
    OsobyPowiazane.__table__.drop(engine)
    Telefon.__table__.drop(engine)
    Email.__table__.drop(engine)
    Adres.__table__.drop(engine)
    Klient.__table__.drop(engine)

# stworzenie tabela
Base.metadata.create_all(engine)

# tworzenie sesji dla danej bazy
Session = sessionmaker(bind=engine)
session = Session()

# wypelanianie bazy danych
k1 = Klient('Maciej','Marciniak')
k2 = Klient('Mariusz','Lesniewski')
k3 = Klient('Marcin','Marciniak')
k4 = Klient('Agnieszka','Nowak')
session.add_all([k1, k2, k3, k4])
session.commit()

session.add_all([
                Adres(k1.id, 'Piwna 3','Znin','88-400'),
                Adres(k1.id, 'Jeleniogorska','Poznan','67-101'),
                Adres(k2.id, 'Bydgoska','Koronowo','92-123'),
                Adres(k2.id, 'Fordonska','Bydgosz','92-076'),
                Adres(k3.id, 'Piwna 3','Znin','88-400'),
				Adres(k4.id, 'Zielna' , 'Podgorzyn', '88-410' )
                ])
session.commit()

session.add_all([
                Email(k1.id,'maciej.r.marciniak@student.put.poznan.pl'),
                Email(k1.id,'mapet19952@wp.pl'),
                Email(k2.id,'maru@lesn.pl'),
                Email(k3.id,'marcin@google.com'),
                ])
session.commit()

session.add_all([
                Telefon(k1.id, '123 123 123'),
                Telefon(k1.id, '890 890 890'),
                Telefon(k3.id, '444-555-666'),
                Telefon(k4.id, '+48777888999'),
                ])
session.commit()

session.add_all([
                OsobyPowiazane(k1.id,k2.id),
                OsobyPowiazane(k2.id,k3.id),
				OsobyPowiazane(k1.id,k4.id),
                ])
session.commit()
# -------------------------------------------------------------------

tryb_szukania = int(input("Szukaj po:\n0 - nazwisko\n1 - imie\n2 - imie i nazwisko\n: "))

if tryb_szukania == 0:
    nazwisko = str(input("Podaj nazwisko: "))
    req = session.query(Klient).filter(Klient.nazwisko==nazwisko)
    if req.first() == None:
        print("Nie ma takiego klienta")
        exit()
    for k in req:
        print("Imie i nazwisko: " + str(k.imie) +" "+ str(k.nazwisko))

    print("Adresy zamieszkania: ")
    for adr in k.adresy:
        print("\t",adr.ulica, adr.miasto, adr.kod_pocztowy)

    print("E-mail: ")
    for em in k.emaile:
        print("\t",em.email, end="\n")

    print("Numery telefonu: ")
    for tel in k.telefony:
        print("\t",tel.telefon)

    print("Osoby powiazane: ")
    for fr in k.friends:
        print("\t",fr.druga_powiazanie.nazwisko, fr.druga_powiazanie.imie)
elif tryb_szukania == 1:
    imie = str(input("Podaj imie: "))
    req = session.query(Klient).filter(Klient.imie==imie)
    if req.first() == None:
        print("Nie ma takiego klienta")
        exit()
    for k in req:
        print("Imie i nazwisko: " + str(k.imie) +" "+ str(k.nazwisko))

    print("Adresy zamieszkania: ")
    for adr in k.adresy:
        print("\t",adr.ulica, adr.miasto, adr.kod_pocztowy)

    print("E-mail: ")
    for em in k.emaile:
        print("\t",em.email)

    print("Numery telefonu: ")
    for tel in k.telefony:
        print("\t",tel.telefon)

    print("Osoby powiazane: ")
    for fr in k.friends:
        print("\t",fr.druga_powiazanie.nazwisko, fr.druga_powiazanie.imie)
elif tryb_szukania == 2:
    nazwisko = str(input("Podaj nazwisko: "))
    imie = str(input("Podaj imie: "))
    req = session.query(Klient).filter(Klient.nazwisko==nazwisko).filter(Klient.imie==imie)
    if req.first() == None:
        print("Nie ma takiego klienta")
        exit()
    for k in req:
        print("\t","Imie i nazwisko: " + str(k.imie) +" "+ str(k.nazwisko))

    print("Adresy zamieszkania: ")
    for adr in k.adresy:
        print("\t",adr.ulica, adr.miasto, adr.kod_pocztowy)

    print("E-mail: ")
    for em in k.emaile:
        print("\t",em.email)

    print("Numery telefonu: ")
    for tel in k.telefony:
        print("\t",tel.telefon)

    print("Osoby powiazane: ")
    for fr in k.friends:
        print("\t",fr.druga_powiazanie.nazwisko, fr.druga_powiazanie.imie)
    print()
else:
    print("Error")



