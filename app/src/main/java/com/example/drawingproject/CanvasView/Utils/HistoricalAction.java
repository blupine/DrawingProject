package com.example.drawingproject.CanvasView.Utils;

public class HistoricalAction {

    //RollbackAction
    protected boolean isOriginalAction; // 기존 액션에 대한 Move, Undo, Redo 등에 해당할 경우 false

    /* 기존 액션일 경우, 그릴 수 있는 활성 상태일 때 true
    *  캔버스에 그릴지 안그릴지 알아야 하므로 isActivated 유지해야 함
    */
    protected boolean isActivated;

    HistoricalAction(){
        this.isOriginalAction = true;
        this.isActivated = true;
    }
}
