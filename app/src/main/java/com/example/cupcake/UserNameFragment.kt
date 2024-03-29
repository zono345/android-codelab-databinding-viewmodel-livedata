/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcake.databinding.FragmentUserNameBinding
import com.example.cupcake.model.OrderViewModel

/**
 * [UserNameFragment] allows the user to choose a pickup date for the cupcake order.
 */
class UserNameFragment : Fragment() {
    private val sharedViewModel: OrderViewModel by activityViewModels()

    // Binding object instance corresponding to the fragment_pickup.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentUserNameBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentUserNameBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            userNameFragment = this@UserNameFragment
        }
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * Navigate to the next screen to see the order summary.
     */
    fun goToNextScreen() {
        // 氏名入力値チェック
        if (!isValidName()) {
            return
        }

        // 画面遷移
        findNavController().navigate(R.id.action_userNameFragment_to_summaryFragment)
    }

    fun cancelOrder() {
        // オーダー変数を初期化
        sharedViewModel.resetOrder()
        // nav_graphでの画面遷移。スタート画面へ移動。
        findNavController().navigate(R.id.action_userNameFragment_to_startFragment)
    }

    // 氏名の入力値チェック
    private fun isValidName(): Boolean {
        val name = sharedViewModel.name.value
        if (name == "") {
            setErrorTextField(true)
            return false
        }
        setErrorTextField(false)
        return true
    }

    // 氏名入力欄のエラー表示更新
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding?.textField?.isErrorEnabled = true
            binding?.textField?.error = getString(R.string.please_enter_your_name)
        } else {
            binding?.textField?.isErrorEnabled = false
        }
    }

    // 注文者の氏名を記録
    fun setUserName() {
        sharedViewModel.setUserName(binding?.textInputEditText?.text.toString())
    }
}