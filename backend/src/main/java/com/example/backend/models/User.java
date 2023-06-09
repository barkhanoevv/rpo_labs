package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity // таблица в базе
@Table(name = "users") //имя это таблицы users
@Access(AccessType.FIELD) // разрешаем доступ к полям класса
public class User {
    public User() { }
    public User(Long id) { this.id = id; }

    public void addMuseum(Museum m) {
        this.museums.add(m);
        m.users.add(this);
    }
    public void removeMuseum(Museum m) {
        this.museums.remove(m);
        m.users.remove(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    @Column(name = "login", nullable = false, unique = true)
    public String login;

    @JsonIgnore
    @Column(name = "password")
    public String password;

    @Transient
    public String np;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @JsonIgnore
    @Column(name = "salt")
    public String salt;

    @Column(name = "token")
    public String token;

    @Column(name = "activity")
    public LocalDateTime activity;

    @ManyToMany(mappedBy = "users")
    public Set<Museum> museums = new HashSet<Museum>();
}
