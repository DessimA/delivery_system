<template>
  <BaseModal :visible="visible" @close="closeModal" :title="modalTitle">
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="nomeProduto">Nome do Produto</label>
        <BaseInput
          id="nomeProduto"
          v-model="form.nomeProduto"
          placeholder="Ex: Pizza Calabresa"
          :error="errors.nomeProduto"
        />
      </div>

      <div class="form-group">
        <label for="descricao">Descrição</label>
        <BaseInput
          id="descricao"
          v-model="form.descricao"
          type="textarea"
          placeholder="Uma breve descrição do produto"
          :error="errors.descricao"
        />
      </div>

      <div class="form-group">
        <label for="preco">Preço</label>
        <BaseInput
          id="preco"
          v-model.number="form.preco"
          type="number"
          step="0.01"
          placeholder="0.00"
          :error="errors.preco"
        />
      </div>

      <div class="form-group">
        <label for="imagem">Imagem do Produto</label>
        <input type="file" @change="handleFileUpload" accept="image/*" />
        <div v-if="form.caminhoImagem" class="image-preview">
          <img :src="form.caminhoImagem" alt="Preview da imagem" />
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
import api from '@/api';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  product: {
    type: Object,
    default: null,
  },
  saving: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'save']);
const { addNotification } = useNotifications();

const defaultForm = {
  idProduto: null,
  nomeProduto: '',
  descricao: '',
  preco: 0,
  caminhoImagem: '',
  imagemFile: null
};

const form = ref({ ...defaultForm });
const errors = ref({});

const isEditing = computed(() => !!form.value.idProduto);
const modalTitle = computed(() => (isEditing.value ? 'Editar Produto' : 'Adicionar Novo Produto'));

watch(() => props.product, (newProduct) => {
  if (newProduct) {
    form.value = { ...newProduct, imagemFile: null };
  } else {
    form.value = { ...defaultForm };
  }
  errors.value = {}; // Clear errors on product change
}, { immediate: true });

watch(() => props.visible, (newVal) => {
  if (!newVal) {
    // Reset form and errors when modal closes
    form.value = { ...defaultForm };
    errors.value = {};
  }
});

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    form.value.imagemFile = file;
    form.value.caminhoImagem = URL.createObjectURL(file);
  }
};

const validateForm = () => {
  let isValid = true;
  errors.value = {};

  if (!form.value.nomeProduto) {
    errors.value.nomeProduto = 'Nome do produto é obrigatório.';
    isValid = false;
  }
  if (!form.value.descricao) {
    errors.value.descricao = 'Descrição é obrigatória.';
    isValid = false;
  }
  if (form.value.preco <= 0) {
    errors.value.preco = 'Preço deve ser maior que zero.';
    isValid = false;
  }

  return isValid;
};

const handleSubmit = async () => {
  if (validateForm()) {
    const productData = { ...form.value };
    if (form.value.imagemFile) {
      const formData = new FormData();
      formData.append('imagem', form.value.imagemFile);
      try {
        const response = await api.post('/upload/imagem', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        productData.caminhoImagem = response.data;
      } catch (error) {
        addNotification({ type: 'error', message: 'Falha ao fazer upload da imagem.' });
        return;
      }
    }
    emit('save', productData);
  } else {
    addNotification({ type: 'error', message: 'Por favor, corrija os erros no formulário.' });
  }
};

const closeModal = () => {
  emit('close');
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

  .base-input { // Targeting the BaseInput component's root element
    width: 100%;
  }
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-lg);
}
</style>