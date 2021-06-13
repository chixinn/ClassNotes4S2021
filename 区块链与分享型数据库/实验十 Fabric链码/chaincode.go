package main

import (
	"fmt"
	"strconv"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
}
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("myChainCode Init")
	_, args := stub.GetFunctionAndParameters()
	var idsum,idmax,idmin string
	var sum,max,min string
	var err error
	// {"Args":["init","idsum","idmax","idmin","sum","max","min"]}
	if len(args)!=5 {
		return shim.Error("Incorrect number of arguments. Expecting 7")
	}
	idsum=args[0]
	idmax=args[1]
	idmin=args[2]
	// 期待一个特殊id globalId
	sum,err=strconv.Atoi(args[3])
	if err != nil {
		return shim.Error("Expecting integer value for global Recording")
	}
	max,err=strconv.Atoi(args[4])
	if err != nil {
		return shim.Error("Expecting integer value for global Recording")
	}
	min,err=strconv.Atoi(args[5])
	if err != nil {
		return shim.Error("Expecting integer value for global Recording")
	}
	fmt.Printf("Init Bill Records: Id = %s, Sum = %d, Max = %d\n, Min = %d\n", id,strconv.Itoa(sum),strconv.Itoa(max),strconv.Itoa(min))
	// Write the state to the ledger
	err = stub.PutState(idsum, []byte(strconv.Itoa(sum)))
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(idmax, []byte(strconv.Itoa(max)))
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(idmax, []byte(strconv.Itoa(min)))
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}
// Invoke方法暴露了向外的调用，是入口
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("myChainCode Invoke")
	function, args := stub.GetFunctionAndParameters()
	if function == "query" {
		return t.query(stub, args)
	} else if function == " querySum" {
		return t.querySum(stub, args)
	} else if function == "queryMax" {
		return t.queryMax(stub, args)
	}
	else if function == "queryMin" {
		return t.queryMin(stub, args)
	}
	else if function == "delete" {
		return t.delete(stub, args)
	}
	else if function == "insert" {
		return t.insert(stub, args)
	}
	return shim.Error("Invalid invoke function name. Expecting \"query\" \"querySum\" \"queryMax\"\"queryMin\"\"delete\"\"insert\"")
}
//根据*id*查询某一条账单的金额 query {"Args":["query","id"]}
func (t *SimpleChaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var id string
	var err error
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting name of the id to query")
	}
	id = args[0]
	amount, err := stub.GetState(id)
	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + id + "\"}"
		return shim.Error(jsonResp)
	}
	if amount == nil {
		jsonResp := "{\"Error\":\"Nil amount for " + id + "\"}"
		return shim.Error(jsonResp)
	}
	jsonResp := "{\"Id\":\"" + id + "\",\"Amount\":\"" + string(amount) + "\"}"
	fmt.Printf("Query Response:%s\n", jsonResp)
	return shim.Success(amount)
}
func (t *SimpleChaincode) insert(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var id string 
	var amount,newMax,newMin,newSum,sum,max,min int
	var err,err_update,err_get,err_put error
	
	// {"Args":["insert","id","amount"]}
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	sumbytes,err_get=stub.GetState("idsum")
	if err_get != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + "idsum" + "\"}"
		return shim.Error(jsonResp)
	}

	maxbytes,err_get=stub.GetState("idmax")
	if err_get != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + "idmax" + "\"}"
		return shim.Error(jsonResp)
	}

	minbytes,err_get=stub.GetState("idmin")
	if err_get != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + "idmin" + "\"}"
		return shim.Error(jsonResp)
	}
	
	id=args[0]
	// //存疑
	// if id==specialId{
	// 	return shim.Error("Wrong Id input!")
	// }//存疑
	amount,err=strconv.Atoi(args[1])
	if err != nil {
		return shim.Error("Expecting integer value for bill recording")
	}
	//update special global Status
	sum,_=strconv.Atoi(sumbytes)
	max,_=strconv.Atoi(maxbytes)
	min,_=strconv.Atoi(minbytes)
	if amount>max{
		newMax=amount
	}
	else{
		newMax=max
	}
	if amount<min{
		newMin=amount
	else{
		newMin=min
	}
	newSum=sum+amount
	fmt.Printf("Insert Bill Records: Id = %s, Amount = %d\n", id, amount)
	// Write the state to the ledger
	err_put = stub.PutState(id, []byte(strconv.Itoa(amount)))
	if err_put!= nil {
		return shim.Error(err.Error())
	}
	// Write the state to the ledger
	err_update=	 stub.PutState("idsum", []byte(strconv.Itoa(newSum)))
	if err_update != nil {
		return shim.Error(err_update.Error())
	}
	err_update=	 stub.PutState("idmax", []byte(strconv.Itoa(newMax)))
	if err_update != nil {
		return shim.Error(err_update.Error())
	}
	err_update=	 stub.PutState("idmin", []byte(strconv.Itoa(newMin)))
	if err_update != nil {
		return shim.Error(err_update.Error())
	}
	return shim.Success(nil)
}
func (t *SimpleChaincode) delete(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	// {"Args":["delete","id"]}
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	id := args[0]
	// Delete the key from the state in ledger
	err := stub.DelState(id)
	if err != nil {
		return shim.Error("Failed to delete state")
	}
	return shim.Success(nil)
}
func (t *SimpleChaincode) querySum(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err,err_get error
	var sum string
	if len(args) != 0 {
		return shim.Error("Incorrect name of opt")
	}
	sum,err_get=stub.GetState("idsum")
	if err_get != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + "idsum" + "\"}"
		return shim.Error(jsonResp)
	}
	if sum == nil {
		jsonResp := "{\"Error\":\"Nil sum for " + "idsum" + "\"}"
		return shim.Error(jsonResp)
	}
	return shim.Success(sum)
}
func (t *SimpleChaincode) queryMax(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err,err_get error
	var max string
	if len(args) != 0 {
		return shim.Error("Incorrect name of opt queryMax")
	}
	max,err_get=stub.GetState("idmax")
	if err_get != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + "idmax" + "\"}"
		return shim.Error(jsonResp)
	}
	if max == nil {
		jsonResp := "{\"Error\":\"Nil max for " + "idmax" + "\"}"
		return shim.Error(jsonResp)
	}
	return shim.Success(max)
}
func (t *SimpleChaincode) queryMin(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err,err_get error
	var min string
	if len(args) != 0 {
		return shim.Error("Incorrect name of opt queryMin")
	}
	min,err_get=stub.GetState("idmin")
	if err_get != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + "idmin" + "\"}"
		return shim.Error(jsonResp)
	}
	if min == nil {
		jsonResp := "{\"Error\":\"Nil min for " + "idmin" + "\"}"
		return shim.Error(jsonResp)
	}
	return shim.Success(min)
}