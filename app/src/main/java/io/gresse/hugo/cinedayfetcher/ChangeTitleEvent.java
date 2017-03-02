package io.gresse.hugo.cinedayfetcher;

import android.support.annotation.Nullable;

/**
 * Requested when we need ot change the Toolbar title
 * <p/>
 * Created by Hugo Gresse on 23/02/2017.
 */
public class ChangeTitleEvent{

    @Nullable
    public String  title;
    @Nullable
    public String  className;
    @Nullable
    public String websiteSlug;

    public ChangeTitleEvent(@Nullable String title, @Nullable String className) {
        this.title = title;
        this.className = className;
    }

}
