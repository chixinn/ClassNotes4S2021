package pbft

type RequestMsg struct {
	Seq      int64  `json:"seq"`
	Operator string `json:"operator"`
}

type PrePrepareMsg struct {
	Seq        int64       `json:"seq"`
	NodeId     int64       `json:"nodeId"`
	RequestMsg *RequestMsg `json:"request"`
	Digest     string      `json:"digest"`
}

type PrepareMsg struct {
	Seq    int64  `json:"seq"`
	NodeId int64  `json:"nodeId"`
	Digest string `json:"digest"`
}

type CommitMsg struct {
	Seq    int64  `json:"seq"`
	NodeId int64  `json:"nodeId"`
	Digest string `json:"digest"`
}

type ReplyMsg struct {
	Seq    int64  `json:"seq"`
	NodeId int64  `json:"nodeId"`
	Digest string `json:"digest"`
}

type SendStatus int

const (
	NoSend SendStatus = iota
	WaitSend
	HasSend
)

type MsgCert struct {
	Seq    int64
	Digest string

	RequestMsg    *RequestMsg
	PrePrepareMsg *PrePrepareMsg
	Prepares      []*PrepareMsg
	Commits       []*CommitMsg

	SendPrePrepare SendStatus
	SendPrepare    SendStatus
	SendCommit     SendStatus
	SendReply      SendStatus
}

func NewMsgCert() *MsgCert {
	return &MsgCert{
		Prepares:       make([]*PrepareMsg, 0),
		Commits:        make([]*CommitMsg, 0),
		SendPrePrepare: NoSend,
		SendPrepare:    NoSend,
		SendCommit:     NoSend,
		SendReply:      NoSend,
	}
}
