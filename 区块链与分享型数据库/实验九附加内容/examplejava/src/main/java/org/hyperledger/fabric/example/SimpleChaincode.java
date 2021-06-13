package org.hyperledger.fabric.example;

import java.util.Arrays;
import java.util.List;
import com.google.protobuf.ByteString;
import io.netty.handler.ssl.OpenSsl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SimpleChaincode extends ChaincodeBase {

    private static Log _logger = LogFactory.getLog(SimpleChaincode.class);


    // finished, do not edit!
    @Override
    public Response init(ChaincodeStub stub) {
        try {
            _logger.info("Init java simple chaincode");
            String func = stub.getFunction();
            if (!func.equals("init")) {
                return newErrorResponse("function other than init is not supported");
            }
            List<String> args = stub.getParameters();
            if (args.size() != 4) {
                newErrorResponse("Incorrect number of arguments. Expecting 4");
            }
            // Initialize the chaincode
            String account1Key = args.get(0);
            int account1Value = Integer.parseInt(args.get(1));
            String account2Key = args.get(2);
            int account2Value = Integer.parseInt(args.get(3));

            _logger.info(String.format("account %s, value = %s; account %s, value %s", account1Key, account1Value, account2Key, account2Value));
            stub.putStringState(account1Key, args.get(1));
            stub.putStringState(account2Key, args.get(3));

            return newSuccessResponse();
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    // finished, do not edit!
    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            _logger.info("Invoke java simple chaincode");
            String func = stub.getFunction();
            List<String> params = stub.getParameters();
            if (func.equals("invoke")) {
                return invoke(stub, params);
            }
            if (func.equals("delete")) {
                return delete(stub, params);
            }
            if (func.equals("query")) {
                return query(stub, params);
            }
            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"invoke\", \"delete\", \"query\"]");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    // todo
    private Response invoke(ChaincodeStub stub, List<String> args) {
        try{
            if (args.size() != 3 ){
                return newErrorResponse("Incorrect number of arguments. Expecting 3");
            }
            String A=args.get(0);
            String B=args.get(1);
            int Aval,Bval;
            int X;
//            Aval=Integer.parseInt(Arrays.toString(stub.getState(A)));
//            Bval=Integer.parseInt(Arrays.toString(stub.getState(B)));
            String AvalBytes = stub.getStringState(A);
            Aval = Integer.parseInt(AvalBytes);
            String BvalBytes = stub.getStringState(B);
            Bval = Integer.parseInt(BvalBytes);
            X=Integer.parseInt(args.get(2));
            Aval=Aval-X;
            Bval=Bval+X;
            _logger.info(String.format("Aval = %d, Bval = %d\n", Aval,Bval));
            stub.putStringState(A, String.valueOf(Aval));
            stub.putStringState(B, String.valueOf(Bval));
            return newSuccessResponse();
        }catch(Throwable e){
            return newErrorResponse(e);
        }
    }

    // todo
    // Deletes an entity from state
    private Response delete(ChaincodeStub stub, List<String> args) {
        try{
            String A;
            if (args.size() != 1 ){
                return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query");
            }
            //Step0
            A=args.get(0);
            //Step1
            stub.delState(A);
            return newSuccessResponse();

        }catch(Throwable e){
            return newErrorResponse(e);
            //java 和go不一样 ，java的报错都放在try catch里面了:D
        }
    }

    // todo
    // query callback representing the query of a chaincode
    private Response query(ChaincodeStub stub, List<String> args) {
        try{
            String A;
            if (args.size() != 1 ){
                return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query");
            }
            //Step0
            A=args.get(0);
            //Step1
//            byte[] Avalbytes=stub.getState(A);
            String Avalbytes = stub.getStringState(A);
//            String jsonResp= "{\"Name\":\"" + A + "\",\"Amount\":\"" + Arrays.toString(Avalbytes) + "\"}";
            String jsonResp = "{\"Name\":\"" + A + "\",\"Amount\":\"" + Avalbytes + "\"}";
            _logger.info("Query Response:%s\n"+jsonResp);
            return newSuccessResponse(Avalbytes, ByteString.copyFrom(Avalbytes, UTF_8).toByteArray());
        }catch(Throwable e){
            return newErrorResponse(e);
        }

    }

    // finished, do not edit!
    public static void main(String[] args) {
        System.out.println("OpenSSL avaliable: " + OpenSsl.isAvailable());
        new SimpleChaincode().start(args);
    }

}
