package config

import "os"

var JWTSecret = os.Getenv("JWT_SECRET")
var ServerPort = os.Getenv("SERVER_PORT")
