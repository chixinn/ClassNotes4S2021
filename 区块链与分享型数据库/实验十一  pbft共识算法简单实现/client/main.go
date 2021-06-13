package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"time"
)

const f = 1

var clientUrl = "localhost:8880"
var requestUrl = "localhost:8881/getRequest"

const from = 100
const to = 104
const waitTime = 5

func main() {
	client := NewClient(clientUrl)
	client.Start()
}

type RequestMsg struct {
	Seq      int64  `json:"seq"`
	Operator string `json:"operator"`
}

type ReplyMsg struct {
	Seq    int64  `json:"seq"`
	NodeId int64  `json:"nodeId"`
	Digest string `json:"digest"`
}

type ReplyCert struct {
	canApply bool
	replys   []*ReplyMsg
}

func NewReplyCert() *ReplyCert {
	return &ReplyCert{
		replys: make([]*ReplyMsg, 0),
	}
}

type Client struct {
	url       string
	replyCert map[int64]*ReplyCert
}

func NewClient(url string) *Client {
	client := &Client{
		url:       url,
		replyCert: make(map[int64]*ReplyCert),
	}
	client.setRoute()

	go client.postTest(requestUrl, from, to)

	return client
}

func (client *Client) Start() {
	fmt.Println(client.url, "listen...")
	err := http.ListenAndServe(client.url, nil)
	if err != nil {
		fmt.Println(err)
		return
	}
}

func (client *Client) setRoute() {
	http.HandleFunc("/getReply", client.getReply)
}

func (client *Client) getReply(writer http.ResponseWriter, request *http.Request) {
	var msg ReplyMsg
	err := json.NewDecoder(request.Body).Decode(&msg)
	if err != nil {
		fmt.Println(err)
		return
	}
	client.handleReply(&msg)
}

func (client *Client) handleReply(msg *ReplyMsg) {
	fmt.Println("\033[32m[Reply]\033[0m msg:", msg)
	cert := client.replyCert[msg.Seq]
	if cert == nil {
		return
	}
	if cert.canApply {
		return
	}
	count := 1
	for _, preMsg := range cert.replys {
		if preMsg.NodeId == msg.NodeId {
			return
		}
		if preMsg.Digest == msg.Digest {
			count++
		}
	}
	cert.replys = append(cert.replys, msg)
	if count >= 2*f+1 {
		cert.canApply = true
	}
}

func (client *Client) Status() {
	fmt.Println("client status:")
	for seq, cert := range client.replyCert {
		fmt.Println("seq", seq, "can apply:", cert.canApply)
	}
}

func (client *Client) postTest(url string, from int, to int) {
	time.Sleep(time.Second * 5)
	for i := from; i < to; i++ {
		seq := int64(i)
		msg := &RequestMsg{
			Seq:      seq,
			Operator: "op" + strconv.Itoa(i),
		}
		jsonMsg, err := json.Marshal(msg)
		if err != nil {
			fmt.Println(err)
			continue
		}
		fmt.Println("\033[31m[Req]\033[0m msg:", msg)
		client.replyCert[msg.Seq] = NewReplyCert()
		postJson(url, jsonMsg)
	}

	time.Sleep(time.Second * waitTime)
	client.Status()
}

func postJson(url string, msg []byte) {
	buf := bytes.NewBuffer(msg)
	http.Post("http://"+url, "application/json", buf)
}
