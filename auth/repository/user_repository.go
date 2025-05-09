package repository

import (
	"auth/config"
	"auth/models"
	"errors"
)

type UserRepository interface {
	SaveUser(user models.User) error
	GetUserByUsername(username string) (models.User, error)
	GetUserByID(id uint) (models.User, error)
}

var currentRepository UserRepository = &defaultRepository{}

type defaultRepository struct{}

func (r *defaultRepository) SaveUser(user models.User) error {
	result := config.DB.Create(&user)
	if result.Error != nil {
		return result.Error
	}
	return nil
}

func (r *defaultRepository) GetUserByUsername(username string) (models.User, error) {
	var user models.User
	result := config.DB.Where("username = ?", username).First(&user)
	if result.Error != nil {
		return models.User{}, errors.New("user not found")
	}
	return user, nil
}

func (r *defaultRepository) GetUserByID(id uint) (models.User, error) {
	var user models.User
	result := config.DB.First(&user, id)
	if result.Error != nil {
		return models.User{}, errors.New("user not found")
	}
	return user, nil
}

func SetRepository(repo UserRepository) {
	currentRepository = repo
}

func GetRepository() UserRepository {
	return currentRepository
}
