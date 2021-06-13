package main

import (
	"fmt"
	"log"
	"os"
	"strconv"

	"simple_pbft/pbft"
)

func init() {
	log.SetFlags(log.Ldate | log.Ltime | log.Lshortfile)
}

func main() {
	id, err := strconv.Atoi(os.Args[1])
	if err != nil {
		fmt.Println(err)
		return
	}

	server := pbft.NewServer(int64(id))
	if server == nil {
		return
	}

	server.Start()
}
