package repository

import (
	"auth/config"
	"auth/models"
	"errors"
)

func SaveUser(user models.User) error {
	result := config.DB.Create(&user)
	if result.Error != nil {
		return result.Error
	}
	return nil
}

func GetUserByUsername(username string) (models.User, error) {
	var user models.User
	result := config.DB.Where("username = ?", username).First(&user)
	if result.Error != nil {
		return models.User{}, errors.New("user not found")
	}
	return user, nil
}
