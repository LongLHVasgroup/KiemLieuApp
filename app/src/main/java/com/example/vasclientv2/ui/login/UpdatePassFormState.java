package com.example.vasclientv2.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class UpdatePassFormState {
    @Nullable
    private Integer newPassError;
    @Nullable
    private Integer repeatPassError;
    private boolean isDataValid;

    UpdatePassFormState( @Nullable Integer newPassError, @Nullable Integer repeatPassError) {

        this.newPassError = newPassError;
        this.repeatPassError = repeatPassError;
        this.isDataValid = false;
    }

    UpdatePassFormState(boolean isDataValid) {
        this.newPassError = null;
        this.repeatPassError = null;
        this.isDataValid = isDataValid;
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