FROM golang:1.24-alpine AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go test ./... -v
RUN go build -o auth-server cmd/main.go

FROM alpine:3.21

WORKDIR /app

COPY --from=builder /app/auth-server .

EXPOSE 8080

CMD ["./auth-server"]