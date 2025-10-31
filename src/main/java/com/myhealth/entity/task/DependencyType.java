package com.myhealth.entity.task;

public enum DependencyType {
    FS, // Finish-to-Start
    SS, // Start-to-Start
    FF, // Finish-to-Finish
    SF  // Start-to-Finish
}