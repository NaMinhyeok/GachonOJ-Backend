package com.gachonoj.problemservice.domain.constant;

import lombok.Getter;

@Getter
public enum ProblemClass {
    BINARY_SEARCH("이분 탐색"),
    GRAPH("그래프"),
    DYNAMIC_PROGRAMMING("동적 계획법"),
    GREEDY("그리디"),
    BRUTE_FORCE("완전 탐색"),
    IMPLEMENTATION("구현"),
    STRING("문자열"),
    MATH("수학"),
    SORT("정렬"),
    DATA_STRUCTURE("자료 구조"),
    DFS_BFS("DFS/BFS"),
    TWO_POINTER("투 포인터"),
    BACKTRACKING("백트래킹"),
    SIMULATION("시뮬레이션"),
    SHORT_PATH("최단 경로"),
    TREE("트리"),
    HASH("해시"),
    ETC("기타");



    private final String label;

    ProblemClass(String label) {
        this.label = label;
    }

    public static ProblemClass fromLabel(String classType) {
        for (ProblemClass problemClass : ProblemClass.values()) {
            if (problemClass.getLabel().equals(classType)) {
                return problemClass;
            }
        }
        return null;
    }
}
