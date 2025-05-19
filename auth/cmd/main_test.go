package main

import (
	"auth/handlers"
	"auth/models"
	"auth/repository"
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/gorilla/mux"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/require"
)

type MockRepository struct {
	mock.Mock
}

func (m *MockRepository) SaveUser(user models.User) error {
	args := m.Called(user)
	return args.Error(0)
}

func (m *MockRepository) GetUserByUsername(username string) (models.User, error) {
	args := m.Called(username)
	return args.Get(0).(models.User), args.Error(1)
}

func (m *MockRepository) GetUserByID(id uint) (models.User, error) {
	args := m.Called(id)
	return args.Get(0).(models.User), args.Error(1)
}

func TestRoutes(t *testing.T) {
	mockRepo := new(MockRepository)

	repository.SetRepository(mockRepo)
	defer repository.SetRepository(nil)

	mockUser := models.User{
		Username: "testuser",
		Hash:     "hashedpassword",
		IsAdmin:  false,
	}

	mockRepo.On("GetUserByUsername", "testuser").Return(mockUser, nil)

	r := mux.NewRouter()
	r.HandleFunc("/register", handlers.RegisterHandler).Methods("POST")
	r.HandleFunc("/login", handlers.LoginHandler).Methods("POST")
	r.HandleFunc("/validate", handlers.ValidateTokenHandler).Methods("GET")
	r.HandleFunc("/user", handlers.GetUserHandler).Methods("GET")
	r.HandleFunc("/user/id", handlers.GetUserByIDHandler).Methods("GET")

	t.Run("Test GetUserHandler", func(t *testing.T) {
		req, err := http.NewRequest("GET", "/user?username=testuser", nil)
		require.NoError(t, err)

		rr := httptest.NewRecorder()
		r.ServeHTTP(rr, req)

		require.Equal(t, http.StatusOK, rr.Code)

		var user models.User
		err = json.NewDecoder(rr.Body).Decode(&user)
		require.NoError(t, err)
		require.Equal(t, "testuser", user.Username)
	})

	t.Run("Test LoginHandler", func(t *testing.T) {
		credentials := map[string]string{
			"username": "testuser",
			"password": "password",
		}
		body, _ := json.Marshal(credentials)

		req, err := http.NewRequest("POST", "/login", bytes.NewBuffer(body))
		require.NoError(t, err)
		req.Header.Set("Content-Type", "application/json")

		rr := httptest.NewRecorder()
		r.ServeHTTP(rr, req)

		require.Equal(t, http.StatusUnauthorized, rr.Code)
	})

	t.Run("Test GetUserByIDHandler", func(t *testing.T) {
		mockRepo.On("GetUserByID", uint(1)).Return(mockUser, nil)

		req, err := http.NewRequest("GET", "/user/id?id=1", nil)
		require.NoError(t, err)

		rr := httptest.NewRecorder()
		r.ServeHTTP(rr, req)

		require.Equal(t, http.StatusOK, rr.Code)

		var user models.User
		err = json.NewDecoder(rr.Body).Decode(&user)
		require.NoError(t, err)
		require.Equal(t, "testuser", user.Username)
	})
}
