package controller;

public interface InputReceiver {

//---  Operations   ---------------------------------------------------------------------------

    public void receiveCode(int code, int mouseType);

    public void receiveKeyInput(char code, int keyType);

}
