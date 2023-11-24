package ui;

public interface InputHandler {

//---  Operations   ---------------------------------------------------------------------------

    public void receiveCode(int code, int mouseType);

    public void receiveKeyInput(char code, int keyType);

}