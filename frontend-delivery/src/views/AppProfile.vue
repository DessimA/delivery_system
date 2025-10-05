<template>
  <div class="profile-container">
    <div class="profile-header" v-if="user">
      <div class="profile-avatar">
        <img :src="user.avatar || '/images/default-avatar.png'" :alt="user.nome" />
        <button class="avatar-edit-btn" @click="addNotification({ type: 'info', message: 'Funcionalidade em desenvolvimento.' })">
          <BaseIcon name="camera" />
        </button>
      </div>
      <div class="profile-info">
        <h1>{{ user.nome }}</h1>
        <p>{{ user.email }}</p>
      </div>
    </div>

    <div class="profile-sections">
      <ProfileSection
        title="Informações Pessoais"
        description="Gerencie seus dados pessoais"
        icon="user"
        @click="openEditModal"
      />

      <ProfileSection
        title="Endereços"
        description="Adicione e edite seus endereços"
        icon="map-pin"
        @click="manageAddresses"
      />

      <ProfileSection
        title="Configurações"
        description="Preferências e notificações"
        icon="settings"
        @click="openSettings"
      />

      <ProfileSection
        title="Sair"
        description="Desconectar da sua conta"
        icon="log-out"
        @click="handleLogout"
      />
    </div>

    <!-- Edit Profile Modal -->
    <BaseModal v-model="isEditModalOpen" title="Editar Informações Pessoais">
      <form @submit.prevent="handleUpdateProfile" class="edit-form">
        <BaseInput v-model="editForm.nome" label="Nome Completo" />
        <BaseInput v-model="editForm.cpf" label="CPF" />
        <BaseInput v-model="editForm.dataNascimento" type="date" label="Data de Nascimento" />
        <BaseInput v-model="editForm.endereco" label="Endereço" />
        <BaseInput v-model="editForm.email" type="email" label="E-mail" />
      </form>
      <template #footer>
        <BaseButton label="Cancelar" variant="secondary" @click="isEditModalOpen = false" />
        <BaseButton label="Salvar" :loading="isUpdating" @click="handleUpdateProfile" />
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useNotifications } from '@/composables/useNotifications';
import { useApi } from '@/composables/useApi';
import api from '@/api';

import ProfileSection from '@/components/profile/ProfileSection.vue';
import BaseIcon from '@/components/base/BaseIcon.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';

const authStore = useAuthStore();
const router = useRouter();
const { addNotification } = useNotifications();
const { loading: isUpdating, execute: executeUpdate } = useApi();

const user = computed(() => authStore.user);

const isEditModalOpen = ref(false);

const editForm = reactive({
  nome: '',
  cpf: '',
  dataNascimento: '',
  endereco: '',
  email: '',
});

const openEditModal = () => {
  if (!user.value) return;
  // Populate form with current user data
  Object.assign(editForm, {
    ...user.value,
    dataNascimento: user.value.dataNascimento ? new Date(user.value.dataNascimento).toISOString().split('T')[0] : '',
  });
  isEditModalOpen.value = true;
};

const handleUpdateProfile = async () => {
  // TODO: Add form validation before submitting
  try {
    const response = await executeUpdate(() => api.put('/usuarios/me', editForm));
    authStore.setUser(response.data); // Update user in the store
    isEditModalOpen.value = false;
    addNotification({ type: 'success', message: 'Perfil atualizado com sucesso!' });
  } catch (err) {
    // Global interceptor in api.js will show error notification
    console.error("Failed to update profile:", err);
  }
};

const manageAddresses = () => {
  addNotification({ type: 'info', message: 'Funcionalidade de gerenciamento de endereços em desenvolvimento.' });
};

const openSettings = () => {
  addNotification({ type: 'info', message: 'Funcionalidade de configurações em desenvolvimento.' });
};

const handleLogout = () => {
  authStore.logout();
  addNotification({ type: 'success', message: 'Você foi desconectado com sucesso.' });
  router.push('/login');
};
</script>

<style lang="scss" scoped>
.profile-container {
  padding: var(--spacing-lg) var(--spacing-md);
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-xl);
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
}

.profile-avatar {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: var(--radius-full);
  overflow: hidden;
  margin-bottom: var(--spacing-md);
  border: 4px solid var(--color-primary);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-edit-btn {
    position: absolute;
    bottom: 0;
    right: 0;
    background-color: var(--color-primary);
    color: var(--color-text-light);
    border-radius: var(--radius-full);
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: var(--font-size-lg);
    border: 2px solid var(--color-background);
    cursor: pointer;
    transition: background-color 0.2s ease;

    &:hover {
      background-color: var(--color-primary-dark);
    }
  }
}

.profile-info {
  h1 {
    font-size: var(--font-size-h3);
    margin-bottom: var(--spacing-xs);
    color: var(--color-text-dark);
  }

  p {
    font-size: var(--font-size-md);
    color: var(--color-dark);
    margin: 0;
  }
}

.profile-sections {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}
</style>