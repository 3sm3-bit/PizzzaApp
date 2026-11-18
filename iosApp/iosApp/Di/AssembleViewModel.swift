//
//  AssembleViewModel.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//
import CoreDomain
import CoreUI
import FeatureAuth
import FeatureHome
import FeatureOrders

func assembleViewModel(uiTayResolver: Resolver) {
    uiTayResolver.register(BaseViewModel.self) {MainActor.assumeIsolated {BaseViewModel()}}
    uiTayResolver.register(AppViewModel.self) {MainActor.assumeIsolated {AppViewModel()}}
    uiTayResolver.register(AuthViewModel.self) {MainActor.assumeIsolated {AuthViewModel()}}
    uiTayResolver.register(HomeViewModel.self) {MainActor.assumeIsolated {HomeViewModel()}}
    uiTayResolver.register(ClientOrderViewModel.self) {MainActor.assumeIsolated {ClientOrderViewModel()}}
}
