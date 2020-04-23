//
// Created by chenweicheng on 2020/4/23.
//
#include "iostream";
#include "thread"

extern "C" {
#include <libavformat/avformat.h>

enum PlayerState {
    IDLE, INIT, PREPARE, PLAYING, PAUSED, STOP, END
};

using namespace std;

class VideoPlayer {

    int mState = IDLE;

    string mUri = nullptr;

    AVFormatContext* mContext = nullptr;

    void setDataSource(string uri){
        mUri = uri;
        mState = INIT;
    }

    void prepare(){
        av_register_all();
        mContext = avformat_alloc_context();
        mState = PREPARE;
    }

    void start(){

    }

};


}