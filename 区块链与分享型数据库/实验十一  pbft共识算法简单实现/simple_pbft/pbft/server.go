package pbft

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

type Server struct {
	node *Node
	url  string
}

func NewServer(id int64) *Server {
	url, ok := NodeTable[id]
	if !ok {
		log.Println("No node with that ID!")
		return nil
	}
	server := &Server{
		node: NewNode(id),
		url:  url,
	}
	server.setRoute()
	return server
}

func (server *Server) Start() {
	log.Printf("%s(node_id=%d) start listen...\n\n", server.url, server.node.id)
	err := http.ListenAndServe(server.url, nil)
	if err != nil {
		fmt.Println(err)
		return
	}
}

func (server *Server) setRoute() {
	http.HandleFunc("/getRequest", server.getRequest)
	http.HandleFunc("/getPrePrepare", server.getPrePrepare)
	http.HandleFunc("/getPrepare", server.getPrepare)
	http.HandleFunc("/getCommit", server.getCommit)
}

func (server *Server) getRequest(writer http.ResponseWriter, request *http.Request) {
	var msg RequestMsg
	err := json.NewDecoder(request.Body).Decode(&msg)
	if err != nil {
		log.Fatalln(err)
		return
	}
	log.Println("<server getRequest> msg:", msg)
	server.node.msgRecvChan <- &msg
}

func (server *Server) getPrePrepare(writer http.ResponseWriter, request *http.Request) {
	var msg PrePrepareMsg
	err := json.NewDecoder(request.Body).Decode(&msg)
	if err != nil {
		log.Fatalln(err)
		return
	}
	log.Println("<server getPrePrepare> msg:", msg)
	server.node.msgRecvChan <- &msg
}

func (server *Server) getPrepare(writer http.ResponseWriter, request *http.Request) {
	var msg PrepareMsg
	err := json.NewDecoder(request.Body).Decode(&msg)
	if err != nil {
		log.Fatalln(err)
		return
	}
	log.Println("<server getPrepare> msg:", msg, "Host:", request.Host)
	server.node.msgRecvChan <- &msg
}

func (server *Server) getCommit(writer http.ResponseWriter, request *http.Request) {
	var msg CommitMsg
	err := json.NewDecoder(request.Body).Decode(&msg)
	if err != nil {
		log.Fatalln(err)
		return
	}
	log.Println("<server getPrepare> msg:", msg)
	server.node.msgRecvChan <- &msg
}
