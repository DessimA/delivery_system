<template>
  <nav class="app-navbar">
    <div class="navbar-container">
      <router-link to="/" class="navbar-brand">
        <img src="/images/logo.svg" alt="DeliveryApp Logo" class="logo" />
        <span>DeliveryApp</span>
      </router-link>

      <div class="navbar-toggle" @click="toggleMobileMenu">
        <Icon :name="isMobileMenuOpen ? 'x' : 'menu'" />
      </div>

      <div :class="['navbar-links', { 'is-open': isMobileMenuOpen }]">
        <router-link to="/products" class="nav-item">Produtos</router-link>
        <router-link to="/cart" class="nav-item cart-icon-wrapper">
          <Icon name="shopping-cart" />
          <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
        </router-link>
        <router-link v-if="authStore.isRestaurantOwner" to="/restaurant/dashboard" class="nav-item" data-testid="restaurant-dashboard-link">Meu Restaurante</router-link>
        <router-link v-if="authStore.isDeliveryUser" to="/delivery/dashboard" class="nav-item" data-testid="delivery-dashboard-link">Minhas Entregas</router-link>
        <router-link v-if="authStore.isLoggedIn" to="/orders" class="nav-item">Meus Pedidos</router-link>

        <div class="navbar-auth">
          <template v-if="!authStore.isLoggedIn">
            <router-link to="/login" class="nav-item">Login</router-link>
            <router-link to="/register" class="nav-item primary">Registrar</router-link>
          </template>
          <template v-else>
            <div class="nav-item user-profile" @click="toggleUserDropdown" data-testid="user-profile-dropdown-toggle">
              <Icon name="user-circle" class="user-avatar" />
              <span>{{ authStore.user?.email || 'Usuário' }}</span>
              <Icon :name="isUserDropdownOpen ? 'chevron-up' : 'chevron-down'" class="dropdown-arrow" />
              <div v-if="isUserDropdownOpen" class="user-dropdown">
                <router-link to="/profile" class="dropdown-item" @click="closeUserDropdown" data-testid="profile-link">
                  <Icon name="user" /> Perfil
                </router-link>
                <div class="dropdown-item" @click="logout" data-testid="logout-link">
                  <Icon name="log-out" /> Sair
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth'; // Será criado
import { useCartStore } from '@/stores/cart'; // Será criado
import BaseIcon from '@/components/base/BaseIcon.vue';

const authStore = useAuthStore();
const cartStore = useCartStore();
const router = useRouter();

const isMobileMenuOpen = ref(false);
const isUserDropdownOpen = ref(false);

const cartItemCount = computed(() => cartStore.itemCount); // Virá do store

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const toggleUserDropdown = () => {
  isUserDropdownOpen.value = !isUserDropdownOpen.value;
};

const closeUserDropdown = () => {
  isUserDropdownOpen.value = false;
};

const handleClickOutside = (event) => {
  if (isUserDropdownOpen.value && !event.target.closest('.user-profile')) {
    isUserDropdownOpen.value = false;
  }
};

const logout = () => {
  authStore.logout();
  router.push('/login');
  closeUserDropdown();
};
</script>

<style lang="scss" scoped>
.app-navbar {
  background-color: var(--color-dark);
  color: var(--color-text-light);
  padding: var(--spacing-sm) var(--spacing-md);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 999;
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-text-light);
  text-decoration: none;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);

  .logo {
    height: 30px; /* Adjust logo size */
  }
}

.navbar-toggle {
  display: none;
  font-size: var(--font-size-xl);
  cursor: pointer;

  @media (max-width: 768px) {
    display: block;
  }
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);

  @media (max-width: 768px) {
    flex-direction: column;
    position: absolute;
    top: 100%;
    left: 0;
    width: 100%;
    background-color: var(--color-dark);
    padding: var(--spacing-md) 0;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    transform: translateY(-100%);
    opacity: 0;
    pointer-events: none;
    transition: transform 0.3s ease-out, opacity 0.3s ease-out;

    &.is-open {
      transform: translateY(0);
      opacity: 1;
      pointer-events: all;
    }
  }
}

.nav-item {
  color: var(--color-text-light);
  text-decoration: none;
  padding: var(--spacing-sm) var(--spacing-xs);
  transition: color 0.2s ease;
  white-space: nowrap;

  &:hover,
  &.router-link-active {
    color: var(--color-primary);
  }

  &.primary {
    background-color: var(--color-primary);
    padding: var(--spacing-sm) var(--spacing-md);
    border-radius: var(--radius-md);
    &:hover {
      background-color: var(--color-primary-dark);
      color: var(--color-text-light);
    }
  }
}

.cart-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;

  .cart-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background-color: var(--color-danger);
    color: var(--color-text-light);
    border-radius: var(--radius-full);
    font-size: var(--font-size-xs);
    padding: 2px 6px;
    min-width: 20px;
    text-align: center;
  }
}

.navbar-auth {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);

  @media (max-width: 768px) {
    flex-direction: column;
    width: 100%;
    gap: var(--spacing-sm);
    margin-top: var(--spacing-md);

    .nav-item {
      width: 100%;
      text-align: center;
    }
  }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  cursor: pointer;
  position: relative;

  .user-avatar {
    font-size: var(--font-size-xl);
  }

  .dropdown-arrow {
    font-size: var(--font-size-sm);
  }

  .user-dropdown {
    position: absolute;
    top: calc(100% + var(--spacing-sm));
    right: 0;
    background-color: var(--color-background);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-md);
    min-width: 160px;
    overflow: hidden;
    z-index: 1000;

    .dropdown-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      padding: var(--spacing-sm) var(--spacing-md);
      color: var(--color-text-dark);
      text-decoration: none;
      white-space: nowrap;

      &:hover {
        background-color: var(--color-surface);
        color: var(--color-primary);
      }
    }
  }
}
</style>
