package main

import (
	"log"
	"net/http"

	"auth/config"
	"auth/handlers"
	"auth/models"
	"auth/repository"

	"github.com/gorilla/mux"
	"golang.org/x/crypto/bcrypt"
)

func main() {
	config.ConnectDB()

	err := config.DB.AutoMigrate(&models.User{})
	if err != nil {
		log.Fatalf("Failed to migrate database: %v", err)
	}

	var userCount int64
	config.DB.Model(&models.User{}).Count(&userCount)
	if userCount == 0 {
		hash, err := bcrypt.GenerateFromPassword([]byte("admin"), bcrypt.DefaultCost)
		if err != nil {
			log.Fatalf("Failed to hash admin password: %v", err)
		}

		admin := models.User{
			Username: "admin",
			Hash:     string(hash),
			IsAdmin:  true,
		}

		err = repository.GetRepository().SaveUser(admin)
		if err != nil {
			log.Fatalf("Failed to create admin user: %v", err)
		}

		log.Println("Admin user created: username=admin, password=admin")
	}

	r := mux.NewRouter()
	r.HandleFunc("/register", handlers.RegisterHandler).Methods("POST")
	r.HandleFunc("/login", handlers.LoginHandler).Methods("POST")
	r.HandleFunc("/validate", handlers.ValidateTokenHandler).Methods("GET")
	r.HandleFunc("/user", handlers.GetUserHandler).Methods("GET")

	log.Println("Server is running on port 8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}
