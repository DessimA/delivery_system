<template>
  <div class="profile-container">
    <div class="profile-header" v-if="user">
      <div class="profile-avatar">
        <img :src="user.avatar || '/images/default-avatar.png'" :alt="user.name" />
        <button class="avatar-edit-btn" @click="addNotification({ type: 'info', message: 'Funcionalidade em desenvolvimento.' })">
          <BaseIcon name="camera" />
        </button>
      </div>
      <div class="profile-info">
        <h1>{{ user.name }}</h1>
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
        <BaseInput id="edit-name" v-model="editForm.name" label="Nome Completo" />
        <BaseInput id="edit-cpf" v-model="editForm.cpf" label="CPF" />
        <BaseInput id="edit-birthDate" v-model="editForm.birthDate" type="date" label="Data de Nascimento" />
        <BaseInput id="edit-address" v-model="editForm.address" label="Endereço" />
        <BaseInput id="edit-email" v-model="editForm.email" type="email" label="E-mail" />
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
import { useAuth } from '@/composables/useAuth';
import { useNotifications } from '@/composables/useNotifications';
import { useApi } from '@/composables/useApi';
import { userService } from '@/services/user.service';

import ProfileSection from '@/components/features/profile/ProfileSection.vue';
import BaseIcon from '@/components/base/BaseIcon.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';

const authStore = useAuthStore();
const { user, logout } = useAuth();
const router = useRouter();
const { addNotification } = useNotifications();
const { loading: isUpdating, execute } = useApi();

const isEditModalOpen = ref(false);

const editForm = reactive({
  name: '',
  cpf: '',
  birthDate: '',
  address: '',
  email: '',
  password: ''
});

const openEditModal = () => {
  if (!user.value) return;
  Object.assign(editForm, {
    name: user.value.name || '',
    cpf: user.value.cpf || '',
    address: user.value.address || '',
    email: user.value.email || '',
    birthDate: user.value.birthDate ? new Date(user.value.birthDate).toISOString().split('T')[0] : '',
    password: ''
  });
  isEditModalOpen.value = true;
};

const handleUpdateProfile = async () => {
  try {
    const payload = { ...editForm };
    if (!payload.password) {
      delete payload.password;
    }
    const updatedUser = await execute(() => userService.updateProfile(payload)); 
    authStore.setUser(updatedUser);
    
    addNotification({ type: 'success', message: 'Perfil atualizado com sucesso!' });
    isEditModalOpen.value = false;
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao atualizar o perfil.' });
  }
};

const manageAddresses = () => {
  addNotification({ type: 'info', message: 'Funcionalidade de gerenciamento de endereços em desenvolvimento.' });
};

const openSettings = () => {
  addNotification({ type: 'info', message: 'Funcionalidade de configurações em desenvolvimento.' });
};

const handleLogout = () => {
  logout();
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