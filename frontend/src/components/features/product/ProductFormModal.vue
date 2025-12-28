<template>
  <BaseModal :modelValue="modelValue" @update:modelValue="closeModal" :title="modalTitle">
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="name">Nome do Produto</label>
        <BaseInput
          id="name"
          v-model="form.name"
          placeholder="Ex: Pizza Calabresa"
          :error="errors.name"
        />
      </div>

      <div class="form-group">
        <label for="description">Descrição</label>
        <BaseInput
          id="description"
          v-model="form.description"
          type="textarea"
          placeholder="Uma breve descrição do produto"
          :error="errors.description"
        />
      </div>

      <div class="form-group">
        <label for="price">Preço</label>
        <BaseInput
          id="price"
          v-model.number="form.price"
          type="number"
          step="0.01"
          placeholder="0.00"
          :error="errors.price"
        />
      </div>

      <div class="form-group">
        <label for="establishment">Estabelecimento</label>
        <select id="establishment" v-model="form.establishmentId" class="base-input">
          <option value="" disabled>Selecione um estabelecimento</option>
          <option v-for="est in establishments" :key="est.id" :value="est.id">
            {{ est.name }}
          </option>
        </select>
        <span v-if="errors.establishmentId" class="error-message">{{ errors.establishmentId }}</span>
      </div>

      <div class="form-group">
        <label for="image">Imagem do Produto</label>
        <input type="file" @change="handleFileUpload" accept="image/*" />
        <div v-if="form.imageUrl" class="image-preview">
          <img :src="getImagePreview(form.imageUrl)" alt="Preview da imagem" />
        </div>
      </div>

      <div class="modal-footer">
        <BaseButton label="Cancelar" variant="secondary" @click="closeModal" />
        <BaseButton type="submit" :label="isEditing ? 'Salvar Alterações' : 'Adicionar Produto'" :loading="saving" />
      </div>
    </form>
  </BaseModal>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import BaseModal from '@/components/base/BaseModal.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';
import { useNotifications } from '@/composables/useNotifications';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  product: {
    type: Object,
    default: null,
  },
  establishments: {
    type: Array,
    default: () => [],
  },
  saving: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:modelValue', 'save']);
const { addNotification } = useNotifications();

const defaultForm = {
  id: null,
  name: '',
  description: '',
  price: 0,
  establishmentId: '',
  imageUrl: '',
  imageFile: null
};

const form = ref({ ...defaultForm });
const errors = ref({});

const isEditing = computed(() => !!form.value.id);
const modalTitle = computed(() => (isEditing.value ? 'Editar Produto' : 'Adicionar Novo Produto'));

watch(() => props.product, (newProduct) => {
  if (newProduct) {
    form.value = { 
        ...newProduct, 
        establishmentId: newProduct.establishmentId || (newProduct.establishment ? newProduct.establishment.id : ''),
        imageFile: null 
    };
  } else {
    form.value = { ...defaultForm };
  }
  errors.value = {}; 
}, { immediate: true });

watch(() => props.modelValue, (newVal) => {
  if (!newVal) {
    form.value = { ...defaultForm };
    errors.value = {};
  }
});

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    form.value.imageFile = file;
    form.value.imageUrl = URL.createObjectURL(file);
  }
};

const getImagePreview = (url) => {
    if (url && !url.startsWith('blob:') && !url.startsWith('http')) {
        const backendUrl = import.meta.env.VITE_APP_BACKEND_URL || 'http://localhost:8080';
        return `${backendUrl}/uploads/${url}`;
    }
    return url;
}

const validateForm = () => {
  let isValid = true;
  errors.value = {};

  if (!form.value.name) {
    errors.value.name = 'Nome do produto é obrigatório.';
    isValid = false;
  }
  if (!form.value.description) {
    errors.value.description = 'Descrição é obrigatória.';
    isValid = false;
  }
  if (form.value.price <= 0) {
    errors.value.price = 'Preço deve ser maior que zero.';
    isValid = false;
  }
  if (!form.value.establishmentId) {
    errors.value.establishmentId = 'Estabelecimento é obrigatório.';
    isValid = false;
  }

  return isValid;
};

const handleSubmit = async () => {
  if (validateForm()) {
    emit('save', { ...form.value });
  } else {
    addNotification({ type: 'error', message: 'Por favor, corrija os erros no formulário.' });
  }
};

const closeModal = () => {
  emit('update:modelValue', false);
};
</script>

<style lang="scss" scoped>
.form-group {
  margin-bottom: var(--spacing-md);

  label {
    display: block;
    margin-bottom: var(--spacing-sm);
    font-weight: var(--font-weight-medium);
    color: var(--color-text-dark);
  }

  .base-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid var(--color-border);
    border-radius: var(--radius-md);
    background-color: var(--color-surface);
    color: var(--color-text-dark);
    font-size: 1rem;
    
    &:focus {
      outline: none;
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
    }
  }

  .error-message {
    color: var(--color-danger);
    font-size: 0.875rem;
    margin-top: 0.25rem;
    display: block;
  }
}

.image-preview {
  margin-top: var(--spacing-sm);
  img {
    max-width: 100%;
    max-height: 200px;
    border-radius: var(--radius-md);
    border: 1px solid var(--color-border);
  }
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-lg);
}
</style>
