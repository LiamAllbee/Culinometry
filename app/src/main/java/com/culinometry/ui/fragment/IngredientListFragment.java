package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.culinometry.databinding.FragmentIngredientListBinding;
import com.culinometry.ui.adapter.IngredientAdapter;
import com.culinometry.viewmodel.IngredientListViewModel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class IngredientListFragment extends Fragment {
    private IngredientAdapter adapter;
    private FragmentIngredientListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIngredientListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        IngredientListViewModel viewModel = new ViewModelProvider(this).get(IngredientListViewModel.class);

        binding.addIngredientButton.setOnClickListener(v -> {
            NavDirections action = IngredientListFragmentDirections.actionToIngredientEditorFragment();

            NavHostFragment.findNavController(this).navigate(action);
        });

        viewModel.getAllIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            IngredientAdapter.OnIngredientClickListener listener = ingredient -> {
                // This is the implementation of the onIngredientClick() using safe args. The Lambda
                // simplifies the override into a shorthand. This isn't actually executed here but
                // is just defining the behavior then handing it to the adapter to use.
                IngredientListFragmentDirections.ActionToIngredientEditorFragment action =
                        IngredientListFragmentDirections.actionToIngredientEditorFragment();

                action.setIngredientId(ingredient.getIngredientId());
                Navigation.findNavController(view).navigate(action);
            };

            adapter = new IngredientAdapter(ingredients, listener);
            binding.ingredientRecyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
