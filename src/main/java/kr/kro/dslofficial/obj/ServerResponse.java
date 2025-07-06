package kr.kro.dslofficial.obj;

import kr.kro.dslofficial.obj.enums.ConnectionStatus;

public class ServerResponse {
    public String response;
    public ConnectionStatus status;
    public ServerResponse(String response, ConnectionStatus status) {
        this.response = response;
        this.status = status;
    }
}
