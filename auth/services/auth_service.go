package services

import (
	"auth/models"
	"auth/repository"
	"auth/utils"
	"errors"

	"golang.org/x/crypto/bcrypt"
)

func RegisterUser(user models.User) error {
	hash, err := bcrypt.GenerateFromPassword([]byte(user.Hash), bcrypt.DefaultCost)
	if err != nil {
		return err
	}
	user.Hash = string(hash)
	return repository.GetRepository().SaveUser(user)
}

func LoginUser(credentials models.Credentials) (string, error) {
	user, err := repository.GetRepository().GetUserByUsername(credentials.Username)
	if err != nil {
		return "", err
	}

	if bcrypt.CompareHashAndPassword([]byte(user.Hash), []byte(credentials.Password)) != nil {
		return "", errors.New("invalid credentials")
	}

	return utils.GenerateJWT(user.Username, int(user.ID), user.IsAdmin)
}

func ValidateToken(token string) (bool, error) {
	_, err := utils.ValidateJWT(token)
	return err == nil, err
}
