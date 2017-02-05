package com.lotusgames.bowsimple;

/**
 * Created by Alexpan98 on 25.12.2016.
 */
public enum BowType {
    NEWBIE_BOW(){
        float radius(int i, int N, float maxR) {
            return maxR;}
    },
    DOTER_BOW(){
        float radius(int i, int N, float maxR) {
            return maxR * 0.5f * (2 - (float)i/N);}
    },
    BOW_WITH_HANDLE(){
        final float handle = 0.15f;
        float radius(int i, int N, float maxR) {
            return maxR * (i < handle*N ? 2 : (1 - 0.5f*(i - N*handle)/(N*(1 - handle))));}
    };

    abstract float radius(int i, int N, float maxR);

    BowType(){}
}
