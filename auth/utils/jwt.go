package utils

import (
	"auth/config"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

type Claims struct {
	Username string `json:"username"`
	UserId   int    `json:"user_id"`
	IsAdmin  bool   `json:"is_admin"`
	jwt.RegisteredClaims
}

func GenerateJWT(username string, userId int, isAdmin bool) (string, error) {
	expirationTime := time.Now().Add(24 * time.Hour)
	claims := &Claims{
		Username: username,
		UserId:   userId,
		IsAdmin:  isAdmin,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expirationTime),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(config.JWTSecret))
}

func ValidateJWT(tokenStr string) (*Claims, error) {
	claims := &Claims{}
	token, err := jwt.ParseWithClaims(tokenStr, claims, func(token *jwt.Token) (any, error) {
		return []byte(config.JWTSecret), nil
	})

	if err != nil || !token.Valid {
		return nil, err
	}

	return claims, nil
}
