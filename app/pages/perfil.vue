<template>
	<div class="min-h-screen flex flex-col items-center mt-12">
		<!-- Header Perfil -->
		<div class="w-full max-w-3xl mt-4 mb-0">
			<div class="bg-white rounded-xl border border-gray-300 p-7 flex flex-row items-center gap-8 relative">
				   <img
					   src="https://images.dog.ceo/breeds/husky/n02110185_1469.jpg"
					   alt="Avatar"
					   class="w-28 h-28 rounded-full object-cover border-2 border-white shadow"
				   />
				<div class="flex-1 flex flex-col gap-1">
					<div class="flex items-center gap-2">
						<h2 class="text-2xl font-bold">Matias Obando</h2>
						<span class="text-gray-500 text-xs flex items-center gap-1">
							<svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a2 2 0 01-2.828 0l-4.243-4.243a8 8 0 1111.314 0z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
							Barcelona, España
							<svg class="w-4 h-4 ml-2" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
							Miembro desde 2025
						</span>
					</div>
					<div class="flex gap-2 mt-2">
						<div class="flex-1 bg-blue-50 border border-blue-200 rounded-lg p-4 flex flex-col items-start min-h-[70px]">
							<div class="flex items-center gap-1 text-blue-600 font-semibold text-sm">
								<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.967a1 1 0 00.95.69h4.178c.969 0 1.371 1.24.588 1.81l-3.385 2.46a1 1 0 00-.364 1.118l1.287 3.966c.3.922-.755 1.688-1.54 1.118l-3.385-2.46a1 1 0 00-1.175 0l-3.385 2.46c-.784.57-1.838-.196-1.54-1.118l1.287-3.966a1 1 0 00-.364-1.118L2.045 9.394c-.783-.57-.38-1.81.588-1.81h4.178a1 1 0 00.95-.69l1.286-3.967z"/></svg>
								Valoración
							</div>
							<div class="text-sm text-gray-700 ml-7">4.8 / 5.0</div>
						</div>
						<div class="flex-1 bg-green-50 border border-green-200 rounded-lg p-4 flex flex-col items-start min-h-[70px]">
							<div class="flex items-center gap-1 text-green-600 font-semibold text-sm">
								<svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M3 3v1a2 2 0 002 2h3.28a2 2 0 011.7.9l.72 1.08a2 2 0 001.7.9h3.28a2 2 0 002-2V3"/></svg>
								Ventas totales
							</div>
							<div class="text-sm text-gray-700 ml-7">12 productos</div>
						</div>
						<div class="flex-1 bg-purple-50 border border-purple-200 rounded-lg p-4 flex flex-col items-start min-h-[70px]">
							<div class="flex items-center gap-1 text-purple-600 font-semibold text-sm">
								<svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M20 13V7a2 2 0 00-2-2H6a2 2 0 00-2 2v6m16 0v6a2 2 0 01-2 2H6a2 2 0 01-2-2v-6m16 0H4"/></svg>
								Productos activos
							</div>
							<div class="text-sm text-gray-700 ml-7">1 productos</div>
						</div>
					</div>
				</div>
				   <button 
					   class="absolute top-4 right-4 bg-white border border-gray-300 rounded-lg px-4 py-2 text-sm font-medium flex items-center gap-2 hover:bg-gray-50 transition"
					   @click="goToEditProfile"
				   >
					   <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15.232 5.232l3.536 3.536M9 11l6 6M3 21h6a2 2 0 002-2v-6a2 2 0 00-2-2H3a2 2 0 00-2 2v6a2 2 0 002 2z"/></svg>
					   Editar perfil
				   </button>
			</div>
		</div>
		<!-- Tabs y productos -->
		<div class="w-full max-w-5xl mt-2">
			<div class="flex bg-white border border-gray-300 rounded-lg overflow-hidden w-full mb-2">
				<button :class="tab==='productos' ? activeTabClass : tabClass" class="flex-1" @click="tab='productos'">Mis productos(1)</button>
				<button :class="tab==='vendidos' ? activeTabClass : tabClass" class="flex-1" @click="tab='vendidos'">Vendidos(0)</button>
				<button :class="tab==='favoritos' ? activeTabClass : tabClass" class="flex-1" @click="tab='favoritos'">Favoritos(0)</button>
			</div>
			<div class="bg-gray-50 rounded-2xl border border-gray-300 p-4 min-h-[200px]">
				<div v-if="tab==='productos'">
					<div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 justify-start">
						<ItemCard :item="{
							id: '1',
							  imagen: 'https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=400&q=80',
							titulo: 'Camiseta Nike beige',
							precioEur: 25,
							marca: 'Nike',
							talla: 'M',
							categoria: 'Camiseta',
							creadoHace: 'hace 2 horas',
							estado: '' // Sin badge
						}" :showBadge="false" :showFav="false" />
					</div>
				</div>
				<div v-else-if="tab==='vendidos'">
					<div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 justify-start">
						<div class="bg-white border border-gray-300 rounded-xl p-5 flex flex-col items-center w-[260px] h-[340px] text-gray-500 text-center justify-center">
							No hay productos vendidos aún.
						</div>
					</div>
				</div>
				<div v-else-if="tab==='favoritos'">
					<div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 justify-start">
						<div class="bg-white border border-gray-300 rounded-xl p-5 flex flex-col items-center w-[260px] h-[340px] text-gray-500 text-center justify-center">
							No tienes favoritos aún.
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
const tab = ref<'productos' | 'vendidos' | 'favoritos'>('productos')
const tabClass = 'px-4 py-2 text-sm text-gray-700 bg-white hover:bg-gray-100 border-none outline-none font-medium transition';
const activeTabClass = 'px-4 py-2 text-sm text-emerald-600 bg-gray-100 border-b-2 border-emerald-400 outline-none font-semibold';

const router = useRouter()
function goToEditProfile() {
	router.push('/editprofile')
}
</script>

<style scoped>
.min-h-screen {
	min-height: 100vh;
}
</style>
