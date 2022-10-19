package com.example.vasclientv2.userconfig;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
public class ChangePassFormState {
    @Nullable
    private Integer currentPassError;
    @Nullable
    private Integer newPassError;
    @Nullable
    private Integer repeatPassError;
    private boolean isDataValid;

    ChangePassFormState(@Nullable Integer currentPassError, @Nullable Integer newPassError, @Nullable Integer repeatPassError) {
        this.currentPassError = currentPassError;
        this.newPassError = newPassError;
        this.repeatPassError = repeatPassError;
        this.isDataValid = false;
    }

    ChangePassFormState(boolean isDataValid) {
        this.currentPassError = null;
        this.newPassError = null;
        this.repeatPassError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getCurrentPassError() {
        return currentPassError;
    }

    @Nullable
    Integer getNewPassError() {
        return newPassError;
    }

    @Nullable
    Integer getRepeatPassError() {
        return repeatPassError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}