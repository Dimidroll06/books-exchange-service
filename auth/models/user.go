package models

import "gorm.io/gorm"

type User struct {
	gorm.Model
	Username string `gorm:"unique;not null" json:"username"`
	Hash     string `gorm:"not null" json:"hash"`
	IsAdmin  bool   `gorm:"default:false" json:"is_admin"`
}

type Credentials struct {
	Username string `json:"username"`
	Password string `json:"password"`
	UserId   int    `json:"user_id"`
}
