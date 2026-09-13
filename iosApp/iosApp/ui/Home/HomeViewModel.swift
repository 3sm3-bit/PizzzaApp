//
//  HomeViewModel.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//
import Shared

@MainActor
class HomeViewModel: BaseViewModel {
    
    private let dataUseCase = KoinHelper.shared.getDataUseCase()
    @Published var pizzaProducts: [ProductModel] = []
    @Published var orders: [ParentOrderModel] = []
    
    func logout() {
        Task{
            await self.execute() {
                try await self.dataUseCase.logout()
            }
        }
    }
}
