package com.example.vasclientv2.ui.login;

import androidx.annotation.Nullable;

import com.example.vasclientv2.model.entities.UserModel;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private UserModel success;
    @Nullable
    private Boolean mustUpdatePass;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable Boolean mustUpdatePass, @Nullable UserModel success) {
        this.mustUpdatePass = mustUpdatePass;
        this.success = success;
    }

    @Nullable
    UserModel getSuccess() {
        return success;
    }

    @Nullable
    Boolean getMustUpdatePass() {
        return mustUpdatePass;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}