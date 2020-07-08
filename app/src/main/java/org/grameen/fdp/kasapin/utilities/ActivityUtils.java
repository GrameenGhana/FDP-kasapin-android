package org.grameen.fdp.kasapin.utilities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.form.fragment.FormModelFragment;
import org.grameen.fdp.kasapin.ui.form.model.MapFormModel;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {
    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static void loadDynamicView(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, String formName) {
        //Todo add parameter to load data from the database, if is in editing mode else display default forms with their resp values
        checkNotNull(fragmentManager);
        checkNotNull(fragment);

        fragmentManager.beginTransaction()
                .replace(R.id.dynamicLayout, fragment, formName)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Returns a retained Fragment that stores the FormModel. The Fragment is associated with the specified <code>FragmentActivity</code>.
     * If the Fragment does not exist yet, an instance will be created and added to the <code>FragmentActivity</code>
     *
     * @param enclosing the <code>FragmentActivity</code> that stores the Fragment
     */
    public static FormModelFragment getFormModelFragment(FragmentActivity enclosing) {
        // find the retained fragment on activity restarts
        FragmentManager fm = enclosing.getSupportFragmentManager();
        FormModelFragment formModelFragment = (FormModelFragment) fm.findFragmentByTag(FormModelFragment.TAG);

        if (formModelFragment == null) {
            // create the retained fragment and data the first time
            formModelFragment = new FormModelFragment();
            fm.beginTransaction().add(formModelFragment, FormModelFragment.TAG).commit();
            formModelFragment.setModel(new MapFormModel());
        }
        return formModelFragment;
    }
}