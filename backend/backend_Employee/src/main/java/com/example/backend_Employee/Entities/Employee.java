    package com.example.backend_Employee.Entities;



    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.persistence.*;

    @Entity  // Indique que cette classe est une entité JPA
    @Table(name = "employees")  // Définit le nom de la table en base de données
    @JsonIgnoreProperties(ignoreUnknown = true) // Ignore les champs JSON non reconnus
    public class Employee {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incrémentation
        private Long id;

        @JsonProperty("firstName")
        @Column(name = "first_name")  // Associe au nom de colonne en base
        private String firstName;

        @JsonProperty("lastName")
        @Column(name = "last_name")
        private String lastName;

        @JsonProperty("email")
        @Column(name = "email", unique = true, nullable = false) // Champ unique et obligatoire
        private String email;

        // Constructeur par défaut (obligatoire pour JPA)
        public Employee() {}

        // Constructeur avec paramètres (optionnel)
        public Employee(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        // Getters et Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
