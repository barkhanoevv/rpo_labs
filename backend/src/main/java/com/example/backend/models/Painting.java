package com.example.backend.models;

import javax.persistence.*;

@Entity // таблица в базе
@Table(name = "paintings") //имя это таблицы paintings
@Access(AccessType.FIELD) // разрешаем доступ к полям класса
public class Painting {

    public Painting() { }
    public Painting(Long id) { this.id = id; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "year")
    public String year;

    @ManyToOne
    @JoinColumn(name="artistid")
    public Artist artist;

    @ManyToOne
    @JoinColumn(name="museumid")
    public Museum museum;
}
