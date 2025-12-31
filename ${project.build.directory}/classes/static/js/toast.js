/**
 * Toast Notification System
 * Displays beautiful, non-intrusive toast messages with auto-dismiss
 */

// Create toast container if it doesn't exist
function ensureToastContainer() {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    return container;
}

/**
 * Show a toast notification
 * @param {string} message - The message to display
 * @param {string} type - Type of toast: 'success', 'error', 'warning', 'info'
 * @param {number} duration - Duration in milliseconds (0 = no auto-dismiss)
 */
function showToast(message, type = 'info', duration = 5000) {
    const container = ensureToastContainer();
    
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    // Get appropriate icon based on type
    const icons = {
        success: '✓',
        error: '✕',
        warning: '⚠',
        info: 'ℹ'
    };
    
    toast.innerHTML = `
        <span class="toast-icon">${icons[type] || icons.info}</span>
        <span class="toast-message">${message}</span>
        <span class="toast-close">×</span>
    `;
    
    container.appendChild(toast);
    
    // Close on click
    toast.addEventListener('click', () => removeToast(toast));
    
    // Auto-dismiss after duration
    if (duration > 0) {
        setTimeout(() => removeToast(toast), duration);
    }
    
    return toast;
}

/**
 * Remove a toast with animation
 */
function removeToast(toast) {
    if (!toast || toast.classList.contains('removing')) return;
    
    toast.classList.add('removing');
    setTimeout(() => {
        if (toast.parentElement) {
            toast.parentElement.removeChild(toast);
        }
    }, 300); // Match animation duration
}

/**
 * Show inline error message below an input field
 * @param {HTMLElement} inputElement - The input field
 * @param {string} message - Error message to display
 */
function showInlineError(inputElement, message) {
    // Clear existing error
    hideInlineError(inputElement);
    
    // Add error class to input
    inputElement.classList.add('error');
    inputElement.classList.remove('success');
    
    // Create error message element
    const errorDiv = document.createElement('div');
    errorDiv.className = 'inline-error show';
    errorDiv.textContent = message;
    errorDiv.setAttribute('data-error-for', inputElement.id || inputElement.name);
    
    // Insert after input
    inputElement.parentNode.insertBefore(errorDiv, inputElement.nextSibling);
}

/**
 * Hide inline error message for an input field
 * @param {HTMLElement} inputElement - The input field
 */
function hideInlineError(inputElement) {
    inputElement.classList.remove('error');
    
    // Find and remove associated error message
    const errorDiv = inputElement.parentNode.querySelector('.inline-error');
    if (errorDiv) {
        errorDiv.remove();
    }
}

/**
 * Mark input as valid
 * @param {HTMLElement} inputElement - The input field
 */
function markInputValid(inputElement) {
    hideInlineError(inputElement);
    inputElement.classList.add('success');
    inputElement.classList.remove('error');
}

/**
 * Clear all validation states from an input
 * @param {HTMLElement} inputElement - The input field
 */
function clearInputValidation(inputElement) {
    inputElement.classList.remove('error', 'success');
    hideInlineError(inputElement);
}
